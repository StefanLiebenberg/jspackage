package org.slieb.jspackage.service.providers;

import org.junit.Before;
import org.junit.Test;
import org.slieb.kute.Kute;
import org.slieb.kute.api.Resource;

import static org.junit.Assert.assertNotNull;

/**
 * /build/defines.js          - A javascript file that contains all relevant properties.
 * /build/cssRenameMap.js     - A css renameMap.
 * /build/deps.js             - A dependendencies file.
 * /build/script.min.js       - A compiled javascript
 * /build/script.sourceMap.js - A sourceMap for the compiled javascript resource.
 */
public class BuildProviderSimpleTest {

    public BuildProvider buildProvider;

    @Before
    public void setUp() throws Exception {
        Resource.Readable basic = Kute.stringResource("/basic.js", "var basic = true;");
        buildProvider = new BuildProvider(Kute.providerOf(basic));
    }

    @Test
    public void definesFileExists() {
        assertNotNull(buildProvider.getResourceByName("/build/defines.js"));
    }


    @Test
    public void depsFileExists() {
        assertNotNull(buildProvider.getResourceByName("/build/deps.js"));
    }

    @Test
    public void renameMapExists() {
        assertNotNull(buildProvider.getResourceByName("/build/cssRenameMap.js"));
    }

    @Test
    public void compiledResourceExists() {
        assertNotNull(buildProvider.getResourceByName("/build/script.cc.js"));
    }

    @Test
    public void sourceMapFileExists() {
        assertNotNull(buildProvider.getResourceByName("/build/script.sourceMap.js"));
    }


}