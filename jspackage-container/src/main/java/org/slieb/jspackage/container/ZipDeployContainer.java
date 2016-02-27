package org.slieb.jspackage.container;

import com.google.template.soy.SoyFileSet;
import org.slieb.kute.providers.ZipStreamResourceProvider;
import org.slieb.throwables.SupplierWithThrowable;

import java.io.IOException;
import java.util.function.Supplier;
import java.util.zip.ZipInputStream;

public class ZipDeployContainer extends StandardLayoutDeployContainer {

    public ZipDeployContainer(final SupplierWithThrowable<ZipInputStream, IOException> zipIOStreamSupplier,
                              final Supplier<SoyFileSet.Builder> soyBuilderSupplier) {
        super(new ZipStreamResourceProvider(zipIOStreamSupplier), soyBuilderSupplier);
    }
}
