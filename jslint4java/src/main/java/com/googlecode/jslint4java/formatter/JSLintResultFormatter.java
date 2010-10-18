package com.googlecode.jslint4java.formatter;

import com.googlecode.jslint4java.JSLintResult;

/**
 * An interface for converting a JSLintResult into a string.
 */
public interface JSLintResultFormatter {

    /**
     * Return the footer for a result formatter. If no footer is required, null will be returned.
     */
    String footer();

    /**
     * Convert {@link JSLintResult} into a suitably formatted String representation.
     */
    String format(JSLintResult result);

    /**
     * Return the header for a result formatter. If no header is required, null will be returned.
     */
    String header();

}
