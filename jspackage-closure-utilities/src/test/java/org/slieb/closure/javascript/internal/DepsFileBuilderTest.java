package org.slieb.closure.javascript.internal;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import slieb.kute.Kute;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.util.Set;

import static java.util.stream.Collectors.toList;


public class DepsFileBuilderTest {

    private Resource.Readable create(String name,
                                     Set<String> requires) {
        return Kute.stringResource("/" + name + ".js", "goog.provide('" + name + "');\n" +
                Joiner.on("\n").join(
                        requires.stream().map(r -> "goog.require('" + r + "');").collect(toList())));
    }

    @Test
    public void testGetDependencyContent() throws Exception {
        Resource.Readable resourceA, resourceB, resourceC, resourceD, baseResource;
        baseResource = Kute.inputStreamResource("/base.js", () -> getClass().getResourceAsStream(
                "/closure-library/closure/goog/base.js"));
        resourceA = create("a", ImmutableSet.of("b", "c", "d"));
        resourceB = create("b", ImmutableSet.of("c", "d"));
        resourceC = create("c", ImmutableSet.of("d"));
        resourceD = create("d", ImmutableSet.of());
        ResourceProvider<Resource.Readable> provider = Kute.providerOf(resourceA, resourceB, resourceC, resourceD,
                                                                       baseResource);

        String content = new DepsFileBuilder(provider).getDependencyContent();

        Assert.assertTrue(content.contains("goog.addDependency('a.js', ['a'], ['b', 'c', 'd']);"));
        Assert.assertTrue(content.contains("goog.addDependency('b.js', ['b'], ['c', 'd']);"));
        Assert.assertTrue(content.contains("goog.addDependency('c.js', ['c'], ['d']);"));
        Assert.assertTrue(content.contains("goog.addDependency('d.js', ['d'], []);"));
        Assert.assertFalse(content.contains("goog.addDependency('base.js', [], []);"));

    }
}