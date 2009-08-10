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
import java.util.Arrays;
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
     * Specifies the names filter of the source files to be used for JSLint. If
     * none are specified, defaults to <code>**</code><code>/*.js</code>.
     *
     * @parameter
     */
    private String[] includes;

    /**
     * Specifies the names filter of the source files to be excluded for JSLint.
     * Maven applies its own defaults.
     *
     * @parameter
     */
    private String[] excludes;

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
        File[] files;
        try {
            files = getFilesToProcess(includes, excludes);
        } catch (IOException e) {
            throw new MojoExecutionException("Error getting files to process",
                    e);
        }
        int failures = 0;
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            failures += lintFile(file);
        }
        if (failures > 0) {
            throw new MojoFailureException("JSLint found " + failures
                    + " problems in " + files.length + " files");
        }
    }

    /**
     * Process includes and excludes to work out which files we ae interested
     * in. Originally nicked from CheckstyleReport, now looks nothing like it.
     */
    private File[] getFilesToProcess(String[] includes, String[] excludes)
            throws IOException {
        ArrayList includeList = new ArrayList();
        ArrayList excludeList = new ArrayList();

        if (includes != null && includes.length == 0) {
            includeList.addAll(Arrays.asList(includes));
        }
        // Defaults.
        if (includeList.isEmpty()) {
            includeList.add("**/*.js");
        }
        getLog().debug("includes=" + includeList);

        if (excludes != null && excludes.length > 0) {
            excludeList.addAll(Arrays.asList(excludes));
        }
        getLog().debug("excludes=" + excludeList);

        String[] defaultExcludes = FileUtils.getDefaultExcludes();
        for (int i = 0; i < defaultExcludes.length; i++) {
            excludeList.add(defaultExcludes[i]);
        }

        String includesStr = StringUtils.join(includeList.iterator(), ",");
        String excludesStr = StringUtils.join(excludeList.iterator(), ",");
        List files = FileUtils.getFiles(sourceDirectory, includesStr,
                excludesStr);
        getLog().debug("files=" + files);

        // How I wish for Java 5.
        return (File[]) files.toArray(new File[files.size()]);
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
