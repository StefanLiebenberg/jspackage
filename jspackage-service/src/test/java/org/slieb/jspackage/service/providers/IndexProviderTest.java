package org.slieb.jspackage.service.providers;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import slieb.kute.Kute;
import slieb.kute.api.Resource;


import java.io.IOException;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


public class IndexProviderTest {

    @Test
    public void testIndex() throws IOException {
        Resource.Readable a, b, c;
        a = Kute.stringResource("/nested/a.js", "A");
        b = Kute.stringResource("/nested/b.js", "B");
        c = Kute.stringResource("/nested/above/e.js", "C");
        Resource.Provider provider = Kute.providerOf(a, b, c);
        IndexProvider indexer = new IndexProvider(provider);

        assertTrue(indexer.getResourceByName("/nested/").isPresent());
        assertTrue(indexer.getResourceByName("/").isPresent());
        assertFalse(indexer.getResourceByName("/nested/a.js").isPresent());
        assertFalse(indexer.getResourceByName("").isPresent());
        assertEquals(ImmutableSet.of("/", "/nested", "/nested/above"),
                     indexer.stream().map(Resource::getPath).collect(Collectors.toSet()));

    }

}