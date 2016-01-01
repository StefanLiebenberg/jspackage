package org.slieb.jspackage.compile.result;


import org.slieb.jspackage.compile.resources.JSModuleResource;
import org.slieb.jspackage.compile.resources.SourceMapResource;

import java.util.Set;

public interface ModuleGroupCompilationResult extends CompileResult {

    class ModuleUnitCompilationResult {

        private final JSModuleResource resource;

        public ModuleUnitCompilationResult(JSModuleResource resource) {
            this.resource = resource;
        }

        public JSModuleResource getResource() {
            return resource;
        }
    }


    class Success implements ModuleGroupCompilationResult {

        private final Set<ModuleUnitCompilationResult> moduleUnits;
        private final SourceMapResource sourceMapResource;

        public Success(Set<ModuleUnitCompilationResult> moduleUnits, SourceMapResource sourceMapResource) {
            this.moduleUnits = moduleUnits;
            this.sourceMapResource = sourceMapResource;
        }


        public Type getType() {
            return Type.SUCCESS;
        }

        public Set<ModuleUnitCompilationResult> getModuleUnits() {
            return moduleUnits;
        }

        public SourceMapResource getSourceMapResource() {
            return sourceMapResource;
        }

    }

    class Failure implements ModuleGroupCompilationResult {

        @Override
        public Type getType() {
            return Type.FAILURE;
        }
    }

}
