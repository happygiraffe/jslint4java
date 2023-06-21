package com.googlecode.jslint4java.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.googlecode.jslint4java.JSLint;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.Option;
import com.googlecode.jslint4java.UnicodeBomInputStream;
import com.googlecode.jslint4java.formatter.CheckstyleXmlFormatter;
import com.googlecode.jslint4java.formatter.JSLintResultFormatter;
import com.googlecode.jslint4java.formatter.JSLintXmlFormatter;
import com.googlecode.jslint4java.formatter.JUnitXmlFormatter;
import com.googlecode.jslint4java.formatter.PlainFormatter;
import com.googlecode.jslint4java.formatter.ReportFormatter;

/**
 * Validates JavaScript using jslint4java.
 *
 * @author dom
 */
// TODO Support alternate jslint
// TODO Support HTML reports (site plugin mojo?)
@Mojo(name = "lint", defaultPhase = LifecyclePhase.VERIFY)
public class JSLintMojo extends AbstractMojo {

    /** Where to write the HTML report. */
    private static final String REPORT_HTML = "report.html";

    /** Where to write the plain text report. */
    private static final String REPORT_TXT = "report.txt";

    /** Where to write the checkstyle report. */
    private static final String CHECKSTYLE_XML = "checkstyle.xml";

    private static final String DEFAULT_INCLUDES = "**/*.js";

    private static final String JSLINT_XML = "jslint.xml";

    /** Where to write the junit report. */
    private static final String JUNIT_XML = "junit.xml";

    /**
     * Specifies the the source files to be excluded for JSLint (relative to
     * {@link #defaultSourceFolder}). Maven applies its own defaults.
     */
    @Parameter(property = "excludes")
    private final List<String> excludes = Lists.newArrayList();

    /**
     * Specifies the the source files to be used for JSLint (relative to
     * {@link #defaultSourceFolder}). If none are given, defaults to <code>**&#47;*.js</code>.
     */
    @Parameter(property = "includes")
    private final List<String> includes = Lists.newArrayList();

    /**
     * Specifies the location of the default source folder to be used for JSLint. Note that this is
     * just used for filling in the default, as it resolves the default value correctly. Anything
     * you specify will override it.
     */
    @Parameter(readonly = true, required = true, defaultValue = "${basedir}/src/main/webapp")
    private File defaultSourceFolder;

    /**
     * Which locations should JSLint look for JavaScript files in? Defaults to
     * ${basedir}/src/main/webapp.
     */
    @Parameter
    private File[] sourceFolders = new File[] {};

    /**
     * Which JSLint {@link Option}s to set.
     */
    @Parameter
    private final Map<String, String> options = Maps.newHashMap();

    /**
     * What encoding should we use to read the JavaScript files? Defaults to UTF-8.
     */
    @Parameter(property = "encoding", defaultValue = "${project.build.sourceEncoding}")
    private String encoding = "UTF-8";

    /**
     * Base folder for report output.
     */
    @Parameter(property = "jslint.outputFolder", defaultValue = "${project.build.directory}/jslint4java")
    private File outputFolder = new File("target");

    /**
     * Fail the build if JSLint detects any problems.
     */
    @Parameter(property = "jslint.failOnError", defaultValue = "true")
    private boolean failOnError = true;

    /**
     * An alternative JSLint to use.
     */
    @Parameter(property = "jslint.source")
    private File jslintSource;

    /**
     * How many seconds JSLint is allowed to run.
     */
    @Parameter(property = "jslint.timeout")
    private long timeout;

    /**
     * Skip linting files if true.
     */
    @Parameter(property = "jslint.skip", defaultValue = "false")
    private boolean skip = false;

    /** Add a single option. For testing only. */
    void addOption(Option sloppy, String value) {
        options.put(sloppy.name().toLowerCase(Locale.ENGLISH), value);
    }

    /**
     * Set the default includes.
     */
    private void applyDefaults() {
        if (includes.isEmpty()) {
            includes.add(DEFAULT_INCLUDES);
        }
        if (sourceFolders.length == 0) {
            sourceFolders = new File[] { defaultSourceFolder };
        }
    }

    private void applyOptions(JSLint jsLint) throws MojoExecutionException {
        for (Entry<String, String> entry : options.entrySet()) {
            if (entry.getValue() == null || entry.getValue().equals("")) {
                continue;
            }
            Option option;
            try {
                option = Option.valueOf(entry.getKey().toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException e) {
                throw new MojoExecutionException("unknown option: " + entry.getKey());
            }
            jsLint.addOption(option, entry.getValue());
        }
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("skipping JSLint");
            return;
        }
        JSLint jsLint = applyJSlintSource();
        applyDefaults();
        applyOptions(jsLint);
        List<File> files = getFilesToProcess();
        int failures = 0;
        ReportWriter reporter = makeReportWriter();
        try {
            reporter.open();
            for (File file : files) {
                JSLintResult result = lintFile(jsLint, file);
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

    private JSLint applyJSlintSource() throws MojoExecutionException {
        JSLintBuilder builder = new JSLintBuilder();
        if (timeout > 0) {
            builder.timeout(timeout);
        }
        if (jslintSource != null) {
            try {
                return builder.fromFile(jslintSource, Charset.forName(encoding));
            } catch (IOException e) {
                throw new MojoExecutionException("Cant' load jslint.js", e);
            }
        } else {
            return builder.fromDefault();
        }
    }

    @VisibleForTesting
    String getEncoding() {
        return encoding;
    }

    /**
     * Process includes and excludes to work out which files we are interested in. Originally nicked
     * from CheckstyleReport, now looks nothing like it.
     *
     * @return a {@link List} of {@link File}s.
     */
    private List<File> getFilesToProcess() throws MojoExecutionException {
        // Defaults.
        getLog().debug("includes=" + includes);
        getLog().debug("excludes=" + excludes);

        List<File> files = Lists.newArrayList();
        for (File folder : sourceFolders) {
            getLog().debug("searching " + folder);
            try {
                files.addAll(new FileLister(folder, includes, excludes).files());
            } catch (IOException e) {
                // Looking in FileUtils, this is a "can never happen". *sigh*
                throw new MojoExecutionException("Error listing files", e);
            }
        }

        return files;
    }

    @VisibleForTesting
    Map<String, String> getOptions() {
        return options;
    }

    private JSLintResult lintFile(JSLint jsLint, File file) throws MojoExecutionException {
        getLog().debug("lint " + file);
        BufferedReader reader = null;
        try {
            UnicodeBomInputStream stream = new UnicodeBomInputStream(new FileInputStream(file));
            stream.skipBOM();
            reader = new BufferedReader(new InputStreamReader(stream, getEncoding()));
            return jsLint.lint(file.toString(), reader);
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("file not found: " + file, e);
        } catch (UnsupportedEncodingException e) {
            // Can never happen.
            throw new MojoExecutionException("unsupported character encoding UTF-8", e);
        } catch (IOException e) {
            throw new MojoExecutionException("problem whilst linting " + file, e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
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

    private ReportWriter makeReportWriter() {
        ReportWriterImpl f1 = new ReportWriterImpl(new File(outputFolder, JSLINT_XML),
                new JSLintXmlFormatter());
        ReportWriterImpl f2 = new ReportWriterImpl(new File(outputFolder, JUNIT_XML),
                new JUnitXmlFormatter());
        ReportWriterImpl f3 = new ReportWriterImpl(new File(outputFolder, CHECKSTYLE_XML),
                new CheckstyleXmlFormatter());
        ReportWriterImpl f4 = new ReportWriterImpl(new File(outputFolder, REPORT_TXT),
                new PlainFormatter());
        ReportWriterImpl f5 = new ReportWriterImpl(new File(outputFolder, REPORT_HTML),
                new ReportFormatter());
        return new MultiReportWriter(f1, f2, f3, f4, f5);
    }

    public void setDefaultSourceFolder(File defaultSourceFolder) {
        this.defaultSourceFolder = defaultSourceFolder;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes.clear();
        this.excludes.addAll(excludes);
    }

    public void setFailOnError(boolean b) {
        failOnError = b;
    }

    public void setIncludes(List<String> includes) {
        this.includes.clear();
        this.includes.addAll(includes);
    }

    /** The location of the JSLint source file. */
    public void setJslint(File jslintSource) {
        this.jslintSource = jslintSource;
    }

    public void setOptions(Map<String, String> options) {
        this.options.clear();
        this.options.putAll(options);
    }

    public void setOutputFolder(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public void setSourceFolders(List<File> sourceFolders) {
        this.sourceFolders = sourceFolders.toArray(new File[sourceFolders.size()]);
    }

}
