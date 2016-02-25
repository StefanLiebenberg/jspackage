package org.slieb.jspackage.service.providers;


import org.slieb.jspackage.dependencies.GoogDependencyCalculator;
import org.slieb.jspackage.dependencies.GoogResources;
import org.slieb.jspackage.service.resources.CssRenameMapResource;
import org.slieb.jspackage.service.resources.DefinesResource;
import org.slieb.jspackage.service.resources.DepsResource;
import org.slieb.kute.api.Resource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Should provide the following files.
 *
 * /build/defines.js          - A javascript file that contains all relevant properties.
 * /build/cssRenameMap.js     - A css renameMap.
 * /build/deps.js             - A dependendencies file.
 * /build/script.min.js       - A compiled javascript
 * /build/script.sourceMap.js - A sourceMap for the compiled javascript resource.
 * /build/style.cc.min        - A compiled stylesheet file.
 */
public class BuildProvider extends AbstractStreamsProvider {

    private final Resource.Provider sources;

    private final GoogDependencyCalculator calculator;

    private final ExternsProvider externsProvider;

    /**
     * @param sources This should contain all javascript files and gss files that are
     *                considered as part of the class path.
     */
    public BuildProvider(Resource.Provider sources) {
        this.sources = sources;
        this.externsProvider = new ExternsProvider();
        this.calculator = GoogResources.getCalculator(this);
    }

    public Properties getProperties() {
        return null;
    }

    public Resource.Readable getDefinesResource() {
        return new DefinesResource("/build/defines.js", Optional.ofNullable(getProperties()));
    }

    public Map<String, String> getCssRenameMap() {
        return null;
    }

    public Resource.Readable getCssRenameMapResource() {
        return new CssRenameMapResource("/build/cssRenameMap.js", Optional.ofNullable(getCssRenameMap()));
    }

    public Resource.Readable getDependenciesFileResource() {
        return new DepsResource("/build/deps.js", this.sources);
    }

    public Resource.Readable getCompiledJavascriptResource() {
        return null;
    }

    public Resource.Readable getJavascriptSourceMapResource() {
        return null;
    }

    public Resource.Readable getStylesheetSourceMapResource() {
        return null;
    }


    public List<? extends Resource.Readable> getResourcesForNamespaceSet(Set<String> namespaces) {
        return calculator.getResourcesFor(namespaces);
    }

    public List<? extends Resource.Readable> getExterns() {
        return externsProvider.stream().collect(Collectors.toList());
    }

    @Override
    protected Stream<Stream<Resource.Readable>> streams() {
        return Stream.of(toolsStream());
    }

    public Stream<Resource.Readable> toolsStream() {
        return Stream.of(getDefinesResource(), getDependenciesFileResource());
//        return Stream.of(compiledSoyStream(), compiledCssStream(), Stream.of(getDepsResource(), getDefinesFile(),
// getCssRenameMap()));
    }

//    public Stream<? extends Resource.Readable> compiledSoyStream() {
////        return soyProvider.stream();
//        return Stream.empty();
//    }
//
//    public Stream<Resource.Readable> compiledCssStream() {
//        return Stream.empty();
//    }
//
//    public Resource.Readable getDepsResource() {
//        return new DepsResource("/deps.js", sourceProvider);
//    }
//
//
//    public Resource.Readable getDefinesFile() {
//        return new DefinesResource("/defines.js", sourceProvider);
//    }


}
