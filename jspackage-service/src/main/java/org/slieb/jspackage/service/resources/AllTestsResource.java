package org.slieb.jspackage.service.resources;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.slieb.closure.dependencies.GoogDependencyNode;
import org.slieb.closure.dependencies.GoogResources;
import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.resources.ContentResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Iterator;

import static slieb.kute.KuteLambdas.all;
import static slieb.kute.KuteLambdas.extensionFilter;


public class AllTestsResource implements ContentResource {

    private static final String ALL_TEST = "/org/slieb/jspackage/service/all_tests.html";

    private final String path;

    private final Resource.Provider sources;
    private final Resource.Provider filterJs;
    private final Resource.Provider filterTests;

    public AllTestsResource(String path,
                            Resource.Provider sources) {
        this.path = path;
        this.sources = sources;
        this.filterJs = Kute.filterResources(this.sources, all(extensionFilter(".js"), extensionFilter("_test.js").negate()::test));
        this.filterTests = Kute.filterResources(this.sources, extensionFilter("_test.html"));
    }

    private String rename(String testpath) {
        return Paths.get(getPath()).relativize(Paths.get(testpath)).toString();
    }

    private String getAllTestsContent() {
        StringBuilder builder = new StringBuilder();
        builder.append("var _allTests = [");
        Iterator<String> iterator = filterTests.stream().map(Resource::getPath).map(this::rename).iterator();
        while (iterator.hasNext()) {
            builder.append("'").append(iterator.next()).append("'");
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append("];");
        return builder.toString();
    }

    private String getScriptsContent() {
        StringBuilder builder = new StringBuilder();
        GoogResources.getCalculator(filterJs)
                .getDependencyResolver()
                .resolveNamespaces(ImmutableList.of("goog.userAgent.product", "goog.testing.MultiTestRunner"))
                .resolve().stream().map(GoogDependencyNode::getResource).map(this::getScriptPath).forEach(
                builder::append);
        return builder.toString();
    }

    private String getScriptPath(Resource.Readable readable) {
        return String.format("<script type='text/javascript' src='%s'></script>", readable.getPath());
    }


    @Override
    public String getContent() throws IOException {
        try (InputStream input = getClass().getResourceAsStream(ALL_TEST)) {
            return IOUtils.toString(input)
                    .replace("$$ALL_TESTS$$", getAllTestsContent())
                    .replace("$$SCRIPTS$$", getScriptsContent());
        }
    }

    @Override
    public String getPath() {
        return path;
    }
}
