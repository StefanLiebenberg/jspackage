package org.slieb.jspackage.compile.providers;

import org.slieb.jspackage.compile.result.CompileResult;
import slieb.kute.api.Resource;

import java.util.stream.Stream;


public class SuccessResourceProvider implements Resource.Provider {

    private final CompileResult.Success success;

    public SuccessResourceProvider(CompileResult.Success success) {
        this.success = success;
    }

    @Override
    public Stream<Resource.Readable> stream() {
        return Stream.of(success.getCompiledResource(), success.getSourceMapResource());
    }
}
