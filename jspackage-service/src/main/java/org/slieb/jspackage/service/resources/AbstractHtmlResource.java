package org.slieb.jspackage.service.resources;


import slieb.kute.api.Resource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public abstract class AbstractHtmlResource implements Resource.Readable {

    public abstract String getHtmlContent();

    @Override
    public Reader getReader() throws IOException {
        return new StringReader(getHtmlContent());
    }
}
