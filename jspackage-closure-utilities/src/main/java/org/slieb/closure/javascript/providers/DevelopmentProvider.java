package org.slieb.closure.javascript.providers;


import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

import java.util.stream.Stream;

/**
 * The development provider gives us a development environment.
 * <p>
 * Features:
 * - It should provide all sources from sourceProvider.
 * - It should compile and rename soy files to javascript.
 */
public class DevelopmentProvider implements ResourceProvider<Resource.Readable> {
//
//    // serve images and css?
//    private final ResourceProvider<? extends Resource.Readable> assetsProvider;
//
//    // merge soy and js here.
//    private final ResourceProvider<? extends Resource.Readable> javascriptProvider;
//
//    // additional scoped stuff.
//    //   _test.js files
//    private final ResourceProvider<? extends Resource.Readable> testsProvider;


    @Override
    public Resource.Readable getResourceByName(String path) {
        return null;
    }

    @Override
    public Stream<Resource.Readable> stream() {
        return null;
    }
}
