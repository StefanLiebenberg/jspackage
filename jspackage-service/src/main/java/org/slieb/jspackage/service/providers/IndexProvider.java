package org.slieb.jspackage.service.providers;


import com.google.common.base.Preconditions;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.*;

public class IndexProvider implements ResourceProvider<Resource.Readable> {

    private final ResourceProvider<Resource.Readable> sources;

    private final ConcurrentHashMap<Path, Set<Path>> map;

    public IndexProvider(ResourceProvider<Resource.Readable> sources) {
        this.sources = sources;
        this.map = new ConcurrentHashMap<>();
    }

    public void index() {
        map.clear();
        map.putAll(
                sources.stream()
                        .map(Resource::getPath)
                        .map(Paths::get)
                        .map(PathIterator::new)
                        .flatMap(p -> StreamSupport.stream(spliteratorUnknownSize(p, 0), false))
                        .filter(p -> p != null && p.getParent() != null)
                        .map(Path::normalize)
                        .distinct()
                        .collect(groupingByConcurrent(Path::getParent, mapping(path -> path, toSet()))));
    }


    public void clear() {
        this.map.clear();
    }

    private Resource.Readable createIndex(Path path) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><body>");
        builder.append("<h1>").append(path).append("</h1>");
        Set<Path> parents = map.keySet();
        map.get(path)
                .stream()
                .map(p -> new Key(p, parents.contains(p) ? Type.DIR : Type.FILE))
                .sorted(Key::compareTo)
                .map(p -> p.path)
                .forEach(p -> builder.append("<li><a href='").append(p.toString()).append("'>").append(p.getFileName()).append("</a></li>"));
        builder.append("</body></html>");
        return Resources.stringResource(path.toString(), builder.toString());

    }

    public Resource.Readable getResourceByName(String path) {
        this.index();
        Path normalPath = Paths.get(path).normalize();
        if (map.containsKey(normalPath)) {
            String indexPath = Paths.get(normalPath.toString(), "index.html").toString();
            Resource.Readable readable = sources.getResourceByName(indexPath);
            if (readable != null) {
                return Resources.rename(path, readable);
            }
            return createIndex(normalPath);
        } else {
            return null;
        }
    }

    @Override
    public Stream<Resource.Readable> stream() {
        this.index();
        return map.keySet().stream().map(this::createIndex);
    }
}

class PathIterator implements Iterator<Path> {

    private Path nextPath;

    public PathIterator(Path nextPath) {
        this.nextPath = nextPath;
    }

    @Override
    public Path next() {
        Preconditions.checkState(nextPath != null);
        Path p = nextPath;
        nextPath = p.getParent();
        return p;
    }

    @Override
    public boolean hasNext() {
        return nextPath != null;
    }
}

class Key implements Comparable<Key> {

    public final Path path;
    public final Type type;

    public Key(Path path, Type type) {
        this.path = path;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Key key = (Key) o;

        if (!path.equals(key.path)) return false;
        return type == key.type;

    }

    @Override
    public int compareTo(Key key) {
        if (key != null) {
            if (type != key.type) {
                return Type.DIR.equals(type) ? -1 : 1;
            }
            return path.compareTo(key.path);
        }
        return 1;
    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}

enum Type {
    FILE, DIR
}