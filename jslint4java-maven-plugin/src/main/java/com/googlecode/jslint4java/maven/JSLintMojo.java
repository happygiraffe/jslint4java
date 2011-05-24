package com.googlecode.jslint4java.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.googlecode.jslint4java.JSLint;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.Option;
import com.googlecode.jslint4java.formatter.JSLintResultFormatter;
import com.googlecode.jslint4java.formatter.PlainFormatter;

/**
 * Validates JavaScript using jslint4java.
 *
 * @author dom
 * @goal check
 * @phase verify
 */
// TODO Support alternate jslint
// TODO Write JUnit XML reports out
// TODO Support HTML reports (site plugin mojo?)
public class JSLintMojo extends AbstractMojo {

    /**
     * Specifies the the source files to be excluded for JSLint (relative to
     * {@link #sourceDirectory}). Maven applies its own defaults.
     *
     * @parameter property="excludes"
     */
    private final List<String> excludes = new ArrayList<String>();

    /**
     * Specifies the the source files to be used for JSLint (relative to {@link #sourceDirectory}).
     * If none are given, defaults to <code>**&#47;*.js</code>.
     *
     * @parameter property="includes"
     */
    private final List<String> includes = new ArrayList<String>();

    private final JSLint jsLint;

    /**
     * Specifies the location of the source directory to be used for JSLint.
     *
     * @parameter expression="${jslint.sourceDirectory}" default-value="${basedir}/src/main/webapp"
     *            property="sourceDirectory"
     * @required
     */
    private File sourceDirectory;

    /**
     * Which JSLint {@link Option}s to set.
     *
     * @parameter
     */
    private final Map<String, String> options = new HashMap<String, String>();

    private final JSLintResultFormatter formatter = new PlainFormatter();

    public JSLintMojo() throws IOException {
        jsLint = new JSLintBuilder().fromDefault();
    }

    /**
     * Set the default includes.
     *
     * @param includes
     */
    private void applyDefaultIncludes(List<String> includes) {
        if (includes.isEmpty()) {
            includes.add("**/*.js");
        }
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!sourceDirectory.exists()) {
            getLog().warn(sourceDirectory + " does not exist");
            return;
        }
        List<File> files = null;
        try {
            applyDefaultIncludes(includes);
            files = getFilesToProcess(includes, excludes);
        } catch (IOException e) {
            // Looking in FileUtils, this is a "can never happen". *sigh*
            throw new MojoExecutionException("Error listing files", e);
        }
        applyOptions();
        int failures = lintFiles(files);
        if (failures > 0) {
            throw new MojoFailureException("JSLint found " + failures + " problems in "
                    + files.size() + " files");
        }
    }

    private void applyOptions() {
        for (Entry<String, String> entry : options.entrySet()) {
            if (entry.getValue() == null || entry.getValue().equals("")) {
                continue;
            }
            Option option = Option.valueOf(entry.getKey().toUpperCase(Locale.ENGLISH));
            jsLint.addOption(option, entry.getValue());
        }
    }

    /**
     * Process includes and excludes to work out which files we are interested in. Originally nicked
     * from CheckstyleReport, now looks nothing like it.
     *
     * @return a {@link List} of {@link File}s.
     */
    private List<File> getFilesToProcess(List<String> includes, List<String> excludes)
            throws IOException {
        // Defaults.
        getLog().debug("includes=" + includes);
        getLog().debug("excludes=" + excludes);

        List<File> files = new FileLister(sourceDirectory, includes, excludes).files();
        getLog().debug("files=" + files);

        return files;
    }

    private JSLintResult lintFile(File file) throws MojoExecutionException {
        getLog().debug("lint " + file);
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            return lintReader(file.toString(), reader);
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("file not found: " + file, e);
        } catch (UnsupportedEncodingException e) {
            // Can never happen.
            throw new MojoExecutionException("unsupported character encoding UTF-8", e);
        } catch (IOException e) {
            throw new MojoExecutionException("problem whilst linting " + file, e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private int lintFiles(List<File> files) throws MojoExecutionException {
        int failures = 0;
        for (File file : files) {
            JSLintResult result = lintFile(file);
            failures += result.getIssues().size();
            logIssues(result);
        }
        return failures;
    }

    private JSLintResult lintReader(String name, Reader reader) throws IOException {
        return jsLint.lint(name, reader);
    }

    private void logIssues(JSLintResult result) {
        String report = formatter.format(result);
        if (report.equals("")) {
            return;
        }
        for (String line : report.split("\n")) {
            getLog().info(line);
        }
    }

    public void setExcludes(List<String> excludes) {
        this.excludes.clear();
        this.excludes.addAll(excludes);
    }

    public void setIncludes(List<String> includes) {
        this.includes.clear();
        this.includes.addAll(includes);
    }

    public void setOptions(Map<String, String> options) {
        this.options.clear();
        this.options.putAll(options);
    }

    public void setSourceDirectory(File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

}
