package org.slieb.closure.gss;

import com.google.common.css.compiler.ast.BasicErrorManager;

/**
 * Created by stefan on 8/13/15.
 */
class GssErrorManager extends BasicErrorManager {

    StringBuffer buffer = new StringBuffer();

    @Override
    public void print(String msg) {
        buffer.append(msg);
    }

}
