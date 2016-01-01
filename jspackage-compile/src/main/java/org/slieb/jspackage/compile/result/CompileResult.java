package org.slieb.jspackage.compile.result;


import org.slieb.jspackage.compile.resources.CompiledResource;
import org.slieb.jspackage.compile.resources.DebugResource;
import org.slieb.jspackage.compile.resources.SourceMapResource;

public interface CompileResult {

    Type getType();

    enum Type {
        SUCCESS, FAILURE
    }

    class Success implements CompileResult {

        private final CompiledResource compiledResource;
        private final SourceMapResource sourceMapResource;
        private final DebugResource errorsResource;

        public Success(CompiledResource compiledResource, SourceMapResource sourceMapResource, DebugResource errorsResource) {
            this.compiledResource = compiledResource;
            this.sourceMapResource = sourceMapResource;
            this.errorsResource = errorsResource;
        }

        @Override
        public Type getType() {
            return Type.SUCCESS;
        }


        public CompiledResource getCompiledResource() {
            return compiledResource;
        }

        public SourceMapResource getSourceMapResource() {
            return sourceMapResource;
        }

        public DebugResource getErrorsResource() {
            return errorsResource;
        }
    }

    class Failure implements CompileResult {

        @Override
        public Type getType() {
            return Type.FAILURE;
        }
    }

}
