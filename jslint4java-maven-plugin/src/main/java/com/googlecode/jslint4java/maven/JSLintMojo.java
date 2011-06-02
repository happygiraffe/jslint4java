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
import com.googlecode.jslint4java.formatter.JSLintXmlFormatter;
import com.googlecode.jslint4java.formatter.PlainFormatter;

/**
 * Validates JavaScript using jslint4java.
 *
 * @author dom
 * @goal lint
 * @phase verify
 */
// TODO Support alternate jslint
// TODO Support HTML reports (site plugin mojo?)
public class JSLintMojo extends AbstractMojo {

    private static final String DEFAULT_INCLUDES = "**/*.js";

    private static final String JSLINT_XML = "jslint.xml";

    /**
     * Specifies the the source files to be excluded for JSLint (relative to
     * {@link #sourceDirectory}). Maven applies its own defaults.
     *
     * @parameter property="excludes"
     */
    private final List<String> excludes = new ArrayList<String>();

    /**
     * Specifies the the source files to be used for JSLint (relative to
     * {@link #sourceDirectory}). If none are given, defaults to
     * <code>**&#47;*.js</code>.
     *
     * @parameter property="includes"
     */
    private final List<String> includes = new ArrayList<String>();

    private final JSLint jsLint;

    /**
     * Specifies the location of the source directory to be used for JSLint.
     *
     * @parameter expression="${jslint.sourceDirectory}"
     *            default-value="${basedir}/src/main/webapp"
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

    /**
     * What encoding should we use to read the JavaScript files?  Defaults to UTF-8.
     *
     * @parameter expression="${encoding}"
     *            default-value="${project.build.sourceEncoding}"
     */
    private String encoding = "UTF-8";

    /**
     * Base directory for report output.
     *
     * @parameter expression="${jslint.outputDirectory}"
     *            default-value="${project.build.directory}"
     */
    private File outputDirectory = new File("target");

    /**
     * Fail the build if JSLint detects any problems.
     *
     * @parameter expression="${jslint.failOnError}" default-value="true"
     */
    private boolean failOnError = true;

    public JSLintMojo() throws IOException {
        jsLint = new JSLintBuilder().fromDefault();
    }

    /**
     * Set the default includes.
     *
     * @param includes
     */
    private void applyDefaults() {
        if (includes.isEmpty()) {
            includes.add(DEFAULT_INCLUDES);
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

    public void execute() throws MojoExecutionException, MojoFailureException {
        applyDefaults();
        if (!sourceDirectory.exists()) {
            getLog().warn(sourceDirectory + " does not exist");
            return;
        }
        List<File> files = null;
        try {
            files = getFilesToProcess(includes, excludes);
        } catch (IOException e) {
            // Looking in FileUtils, this is a "can never happen". *sigh*
            throw new MojoExecutionException("Error listing files", e);
        }
        applyOptions();
        int failures = 0;
        ReportWriter reporter = new ReportWriter(new File(outputDirectory, JSLINT_XML), new JSLintXmlFormatter());
        try {
            reporter.open();
            for (File file : files) {
                JSLintResult result = lintFile(file);
                failures += result.getIssues().size();
                logIssues(result, reporter);
            }
        } finally {
            reporter.close();
        }
        if (failures > 0) {
            String message = "JSLint found " + failures + " problems in " + files.size() + " files";
            if (failOnError) {
                throw new MojoFailureException(message);
            } else {
                getLog().info(message);
            }
        }
    }

    // Visible for testing only.
    String getEncoding() {
        return encoding;
    }

    /**
     * Process includes and excludes to work out which files we are interested
     * in. Originally nicked from CheckstyleReport, now looks nothing like it.
     *
     * @return a {@link List} of {@link File}s.
     */
    private List<File> getFilesToProcess(List<String> includes, List<String> excludes)
            throws IOException {
        // Defaults.
        getLog().debug("includes=" + includes);
        getLog().debug("excludes=" + excludes);

        return new FileLister(sourceDirectory, includes, excludes).files();
    }

    // Visible for testing only.
    Map<String, String> getOptions() {
        return options;
    }

    private JSLintResult lintFile(File file) throws MojoExecutionException {
        getLog().debug("lint " + file);
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, getEncoding()));
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

    private JSLintResult lintReader(String name, Reader reader) throws IOException {
        return jsLint.lint(name, reader);
    }

    private void logIssues(JSLintResult result, ReportWriter reporter) {
        reporter.report(result);
        if (result.getIssues().isEmpty()) {
            return;
        }
        logIssuesToConsole(result);
    }

    private void logIssuesToConsole(JSLintResult result) {
        JSLintResultFormatter formatter = new PlainFormatter();
        String report = formatter.format(result);
        for (String line : report.split("\n")) {
            getLog().info(line);
        }
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
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

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setSourceDirectory(File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public void setFailOnError(boolean b) {
        failOnError  = b;
    }

}
