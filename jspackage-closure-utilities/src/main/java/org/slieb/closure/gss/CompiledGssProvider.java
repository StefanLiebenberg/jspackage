package org.slieb.closure.gss;

import com.google.common.css.JobDescription;
import com.google.common.css.JobDescriptionBuilder;
import com.google.common.css.SubstitutionMapProvider;
import com.google.common.css.compiler.ast.CssTree;
import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.KuteLambdas;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;


public class CompiledGssProvider implements Resource.Provider {

    private final Resource.Provider gssProvider;

    private final SubstitutionMapProvider renameMapProvider;

    private final Map<String, Set<String>> compilesMap;

    public CompiledGssProvider(Map<String, Set<String>> compilesMap,
                               SubstitutionMapProvider renameMapProvider,
                               Resource.Provider provider) {
        this.compilesMap = compilesMap;
        this.renameMapProvider = renameMapProvider;
        this.gssProvider = Kute.filterResources(provider, KuteLambdas.extensionFilter(".gss"));
    }

    @Override
    public Optional<Resource.Readable> getResourceByName(String path) {

        if (compilesMap.containsKey(path)) {
            return Optional.of(compileGss(path, compilesMap.get(path)));
        } else {
            return Optional.empty();
        }
    }


    @Override
    public Stream<Resource.Readable> stream() {
        return compilesMap.entrySet().stream().map(entry -> compileGss(entry.getKey(), entry.getValue()));
    }

    public GssCompiledResource compileGss(String inputPath,
                                          Set<String> namespaces) {
        return new GssCompiledResource(inputPath, getDependencyProvider(namespaces), this::preProcess, null,
                getJobDescription());
    }

    private GssDependencyProvider getDependencyProvider(Set<String> namespaces) {
        return new GssDependencyProvider(gssProvider, namespaces);
    }

    private JobDescription getJobDescription() {
        final JobDescriptionBuilder jobBuilder = new JobDescriptionBuilder();
        if (renameMapProvider != null) {
            jobBuilder.setCssSubstitutionMapProvider(renameMapProvider);
        }
        return jobBuilder.getJobDescription();
    }


    private URLFunctionCssCompilerPass preProcess(final CssTree tree) {
        final DefaultGssUrlConfiguration configuration = new DefaultGssUrlConfiguration(null, null);
        return new URLFunctionCssCompilerPass(tree, configuration);
    }


}


