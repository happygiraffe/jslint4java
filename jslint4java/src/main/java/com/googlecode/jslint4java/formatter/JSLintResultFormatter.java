package com.googlecode.jslint4java.formatter;

import com.googlecode.jslint4java.JSLintResult;

public interface JSLintResultFormatter {

    /**
     * Convert {@link JSLintResult} into a suitably formatted String representation.
     */
    String format(JSLintResult result);

}
