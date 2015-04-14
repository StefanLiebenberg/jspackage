package org.slieb.closure.javascript;

import com.google.common.collect.ImmutableSet;
import com.google.javascript.jscomp.SourceFile;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;

import static com.google.javascript.jscomp.SourceFile.fromFile;
import static org.apache.commons.io.FileUtils.iterateFiles;

public class GoogResources {
    
    public static Supplier<Iterable<SourceFile>> getSourceFileIterableSupplierFromDirectories(Set<File> directories) {
        ImmutableSet.Builder<SourceFile> immutableList = new ImmutableSet.Builder<>();
        directories.forEach(directory -> {
            Iterator<File> iterator = iterateFiles(directory, new String[]{".js"}, true);
            while (iterator.hasNext()) {
                immutableList.add(fromFile(iterator.next()));
            }
        });
        return immutableList::build;
    }

}
