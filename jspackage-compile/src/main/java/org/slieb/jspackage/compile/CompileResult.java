package org.slieb.jspackage.compile;


import slieb.kute.api.Resource;

public interface CompileResult {

    public Type getType();

    enum Type {
        SUCCESS, FAILURE
    }

    class Success implements CompileResult {

        Resource.Readable readable;

        public Success(Resource.Readable readable) {
            this.readable = readable;
        }

        @Override
        public Type getType() {
            return Type.SUCCESS;
        }
    }

    class Failure implements CompileResult {

        @Override
        public Type getType() {
            return Type.FAILURE;
        }
    }

}
