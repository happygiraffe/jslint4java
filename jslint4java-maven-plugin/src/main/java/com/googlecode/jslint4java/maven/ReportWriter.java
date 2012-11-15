package com.googlecode.jslint4java.maven;

import java.io.Closeable;

import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.formatter.JSLintResultFormatter;

/**
 * Write a report formatted by a {@link JSLintResultFormatter} to somewhere. You
 * should call the methods in this order:
 * <ol>
 * <li>{@link #open()}
 * <li>{@link #report(JSLintResult)}
 * <li>{@link #close()}
 * </ol>
 *
 * <p>
 * If you don't call close(), you won't see any output.
 */
public interface ReportWriter extends Closeable {

    /** End the report. */
    void close();

    /** Begin the report. Call immediately after construction. */
    void open();

    /** Report the result of a run. */
    void report(JSLintResult result);

}