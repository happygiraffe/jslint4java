package net.happygiraffe.jslint.ant;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import net.happygiraffe.jslint.Issue;

/**
 * Output all issues found somewhere. The calling sequence is:
 *
 * <ul>
 * <li>{@link #setOut(OutputStream)}
 * <li>{@link #begin()}
 * <li>{@link #output(File, List)}
 * <li>{@link #end()}
 * </ul>
 *
 * @author dom
 * @version $Id$
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
     * @param file
     *                The file just examined.
     *
     * @param issues
     *                A list of issues fond with this file. May be empty.
     */
    public abstract void output(File file, List<Issue> issues);

    /**
     * Called during initialization.
     *
     * @param os
     */
    public abstract void setOut(OutputStream os);

}