package org.slieb.jspackage.service.providers;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;
import slieb.kute.resources.Resources;

import java.io.IOException;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by stefan on 5/11/15.
 */
public class IndexProviderTest {

    @Test
    public void testIndex() throws IOException {
        Resource.Readable a, b, c;
        a = Resources.stringResource("/nested/a.js", "A");
        b = Resources.stringResource("/nested/b.js", "B");
        c = Resources.stringResource("/nested/above/e.js", "C");
        ResourceProvider<Resource.Readable> provider = Resources.providerOf(a, b, c);
        IndexProvider indexer = new IndexProvider(provider);

        assertNotNull(indexer.getResourceByName("/nested/"));
        assertNotNull(indexer.getResourceByName("/"));
        assertNull(indexer.getResourceByName("/nested/a.js"));
        assertNull(indexer.getResourceByName(""));

        assertEquals(ImmutableSet.of("/", "/nested", "/nested/above"), indexer.stream().map(Resource::getPath).collect(Collectors.toSet()));

    }

}