package com.googlecode.jslint4java.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLint;

/**
 * Validates JavaScript using jslint4java.
 *
 * @author dom
 * @goal check
 * @phase verify
 */
public class JSLintMojo extends AbstractMojo {

    /**
     * Specifies the the source files to be used for JSLint (relative to
     * {@link #sourceDirectory}). If none are given, defaults to <code>**</code>
     * <code>/*.js</code>.
     *
     * @parameter
     */
    private final List includes = new ArrayList();

    /**
     * Specifies the the source files to be excluded for JSLint (relative to
     * {@link #sourceDirectory}). Maven applies its own defaults.
     *
     * @parameter
     */
    private final List excludes = new ArrayList();

    /**
     * Specifies the location of the source directory to be used for JSLint.
     *
     * @parameter expression="${jslint.sourceDirectory}"
     *            default-value="${basedir}/src/main/webapp"
     * @required
     */
    private File sourceDirectory;

    private final JSLint jsLint;

    public JSLintMojo() throws IOException {
        jsLint = new JSLint();
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!sourceDirectory.exists()) {
            getLog().warn(sourceDirectory + " does not exist");
            return;
        }
        List files = null;
        try {
            files = getFilesToProcess(includes, excludes);
        } catch (IOException e) {
            // Looking in FileUtils, this is a "can never happen". *sigh*
            throw new MojoExecutionException("Error listing files", e);
        }
        int failures = 0;
        Iterator it = files.iterator();
        while (it.hasNext()) {
            File file = (File) it.next();
            failures += lintFile(file);
        }
        if (failures > 0) {
            throw new MojoFailureException("JSLint found " + failures
                    + " problems in " + files.size() + " files");
        }
    }

    /**
     * Process includes and excludes to work out which files we ae interested
     * in. Originally nicked from CheckstyleReport, now looks nothing like it.
     *
     * @return a {@link List} of {@link File}s.
     */
    private List getFilesToProcess(List includes, List excludes)
            throws IOException {
        // Defaults.
        if (includes.isEmpty()) {
            includes.add("**/*.js");
        }
        getLog().debug("includes=" + includes);
        getLog().debug("excludes=" + excludes);

        String[] defaultExcludes = FileUtils.getDefaultExcludes();
        for (int i = 0; i < defaultExcludes.length; i++) {
            excludes.add(defaultExcludes[i]);
        }

        String includesStr = StringUtils.join(includes.iterator(), ",");
        String excludesStr = StringUtils.join(excludes.iterator(), ",");
        List files = FileUtils.getFiles(sourceDirectory, includesStr,
                excludesStr);
        getLog().debug("files=" + files);

        // How I wish for Java 5.
        return files;
    }

    private int lintFile(File file) throws MojoExecutionException {
        getLog().debug("lint " + file);
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    stream, "UTF-8"));
            List issues = jsLint.lint(file.toString(), reader);
            Iterator it = issues.iterator();
            while (it.hasNext()) {
                Issue issue = (Issue) it.next();
                logIssue(issue);
            }
            return issues.size();
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("file not found", e);
        } catch (UnsupportedEncodingException e) {
            throw new MojoExecutionException(
                    "unsupported character encoding UTF-8", e);
        } catch (IOException e) {
            throw new MojoExecutionException("aaaragh", e);
        }
    }

    private void logIssue(Issue issue) {
        getLog().info(issue.toString());
        getLog().info(issue.getEvidence());
        getLog().info(spaces(issue.getCharacter() - 1) + "^");
    }

    protected String spaces(int howmany) {
        StringBuffer sb = new StringBuffer(howmany);
        for (int i = 0; i < howmany; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

}
