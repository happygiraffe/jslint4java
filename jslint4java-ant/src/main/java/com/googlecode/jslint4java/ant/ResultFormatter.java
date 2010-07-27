package com.googlecode.jslint4java.ant;

import java.io.File;
import java.io.OutputStream;

import com.googlecode.jslint4java.JSLintResult;

/**
 * Output all issues found somewhere. The calling sequence is:
 *
 * <ul>
 * <li>{@link #setStdout(OutputStream)}
 * <li>{@link #setFile(File)}
 * <li>{@link #begin()}
 * <li>{@link #output(JSLintResult)}
 * <li>{@link #end()}
 * </ul>
 *
 * @author dom
 */
public interface ResultFormatter {

    /**
     * Called at the start of {@link JSLintTask} execution.
     */
    public abstract void begin();

    /**
     * Called at the end of {@link JSLintTask} execution.
     */
    public abstract void end();

    /**
     * Called for each file that is checked by {@link JSLintTask}.
     *
     * @param result
     *            The details if this JSLint run.
     */
    public abstract void output(JSLintResult result);

    /**
     * Called during initialization. The file to where output should be written.
     *
     * @param file
     *            The file to write to.
     */
    public abstract void setFile(File file);

    /**
     * If you want to write to stdout, you can't just use System.out, because we're in the middle of
     * ant task. Ensure that a suitable form is passed down instead.
     */
    public abstract void setStdout(OutputStream defaultOutputStream);

}