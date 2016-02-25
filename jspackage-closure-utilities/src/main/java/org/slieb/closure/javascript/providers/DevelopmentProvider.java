package org.slieb.closure.javascript.providers;


import org.slieb.kute.api.Resource;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * The development provider gives us a development environment.
 * <p>
 * Features:
 * - It should provide all sources from sourceProvider.
 * - It should compile and rename soy files to javascript.
 */
public class DevelopmentProvider implements Resource.Provider {
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
    public Optional<Resource.Readable> getResourceByName(String path) {
        return Optional.empty();
    }

    @Override
    public Stream<Resource.Readable> stream() {
        return Stream.empty();
    }
}
