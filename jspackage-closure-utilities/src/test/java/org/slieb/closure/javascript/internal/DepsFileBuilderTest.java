package org.slieb.closure.javascript.internal;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import junit.framework.Assert;
import org.junit.Test;
import org.slieb.closure.javascript.GoogDependencyParser;
import org.slieb.closure.javascript.GoogResources;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;

import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * Created by stefan on 5/8/15.
 */
public class DepsFileBuilderTest {

    private Resource.Readable create(String name, Set<String> requires) {
        return Resources.stringResource("/" + name + ".js", new StringBuilder()
                .append("goog.provide('").append(name).append("');\n")
                .append(Joiner.on("\n").join(requires.stream().map(r -> "goog.require('" + r + "');").collect(toList())))
                .toString());
    }

    @Test
    public void testGetDependencyContent() throws Exception {
        Resource.Readable resourceA, resourceB, resourceC, resourceD, baseResource;
        baseResource = Resources.inputStreamResource("/base.js", () -> getClass().getResourceAsStream("/closure-library/closure/goog/base.js"));
        resourceA = create("a", ImmutableSet.of("b", "c", "d"));
        resourceB = create("b", ImmutableSet.of("c", "d"));
        resourceC = create("c", ImmutableSet.of("d"));
        resourceD = create("d", ImmutableSet.of());
        ResourceProvider<Resource.Readable> provider = Resources.providerOf(resourceA, resourceB, resourceC, resourceD, baseResource);

        GoogDependencyParser<Resource.Readable> parser = GoogResources.getDependencyParser();
        String content = new DepsFileBuilder(provider, parser).getDependencyContent();

        Assert.assertTrue(content.contains("goog.addDependency('a.js', ['a'], ['b', 'c', 'd']);"));
        Assert.assertTrue(content.contains("goog.addDependency('b.js', ['b'], ['c', 'd']);"));
        Assert.assertTrue(content.contains("goog.addDependency('c.js', ['c'], ['d']);"));
        Assert.assertTrue(content.contains("goog.addDependency('d.js', ['d'], []);"));
        Assert.assertFalse(content.contains("goog.addDependency('base.js', [], []);"));

    }
}