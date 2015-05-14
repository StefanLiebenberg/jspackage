package org.slieb.jspackage.service.resources;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.slieb.closure.dependencies.GoogDependencyNode;
import org.slieb.closure.dependencies.GoogResources;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.Iterator;

import static slieb.kute.resources.ResourcePredicates.all;
import static slieb.kute.resources.ResourcePredicates.extensionFilter;
import static slieb.kute.resources.Resources.filterResources;


public class AllTestsResource implements Resource.Readable {

    private static final String ALL_TEST = "/org/slieb/jspackage/service/all_tests.html";

    private final String path;

    private final ResourceProvider<? extends Readable> sources;
    private final ResourceProvider<? extends Readable> filterJs;
    private final ResourceProvider<? extends Readable> filterTests;

    public AllTestsResource(String path, ResourceProvider<? extends Readable> sources) {
        this.path = path;
        this.sources = sources;
        this.filterJs = filterResources(this.sources, all(extensionFilter(".js"), extensionFilter("_test.js").negate()));
        this.filterTests = filterResources(this.sources, extensionFilter("_test.html"));
    }

    private String rename(String testpath) {
        return Paths.get(path).relativize(Paths.get(testpath)).toString();
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
        GoogResources.getCalculatorCast(filterJs).getDependencyResolver()
                .resolveNamespaces(ImmutableList.of("goog.userAgent.product", "goog.testing.MultiTestRunner"))
                .resolve().stream().map(GoogDependencyNode::getResource).map(this::getScriptPath).forEach(builder::append);
        return builder.toString();
    }

    private String getScriptPath(Resource.Readable readable) {
        return String.format("<script type='text/javascript' src='%s'></script>", readable.getPath());
    }

    @Override
    public Reader getReader() throws IOException {
        try (InputStream input = getClass().getResourceAsStream(ALL_TEST)) {
            return new StringReader(IOUtils.toString(input)
                    .replace("$$ALL_TESTS$$", getAllTestsContent())
                    .replace("$$SCRIPTS$$", getScriptsContent()));
        }
    }

    @Override
    public String getPath() {
        return path;
    }
}
