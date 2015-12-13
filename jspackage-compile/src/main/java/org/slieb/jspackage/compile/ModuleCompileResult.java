package org.slieb.jspackage.compile;


import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

public interface ModuleCompileResult extends CompileResult {

    class Success implements ModuleCompileResult {

        private final ResourceProvider<Resource.Readable> outputResources;

        public Success(ResourceProvider<Resource.Readable> outputResources) {
            this.outputResources = outputResources;
        }

        public ResourceProvider<Resource.Readable> getOutputResources() {
            return outputResources;
        }


        public Type getType() {
            return Type.SUCCESS;
        }
    }

    class Failure implements ModuleCompileResult {

        @Override
        public Type getType() {
            return Type.FAILURE;
        }
    }

}
