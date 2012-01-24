package com.googlecode.jslint4java.cli;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.cli.Main.DieException;

public class MainTest {

    private static final String NEWLINE = System.getProperty("line.separator");
    private static final String RESOURCE_PREFIX = "com/googlecode/jslint4java/";
    private static final List<String> NO_OUTPUT = ImmutableList.of();

    @Rule
    public StdioResource stdio = new StdioResource();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private final Main main = new Main();

    private void assertDied(DieException e, int code, Matcher<String> message) {
        assertThat(e.getCode(), is(code));
        assertThat(e.getMessage(), message);
    }

    private void assertDiedHappily(DieException e) {
        assertDied(e, 0, nullValue(String.class));
        // If we exited happily, we shouldn't be putting anything on stderr.
        assertThat(stdio.getStderr(), is(""));
    }

    /** Check that the exit value, stdout and stderr are as expected. */
    private void assertLintOutput(int actualExit, int expectedExit, List<String> expectedStdout,
            List<String> expectedStderr) {
        Joiner nl = Joiner.on(NEWLINE);
        assertThat(stdio.getStdout(), is(nl.join(maybeAddTrailer(expectedStdout))));
        assertThat(stdio.getStderr(), is(nl.join(maybeAddTrailer(expectedStderr))));
        // Do this last so that we see stdout/stderr errors first.
        assertThat(actualExit, is(expectedExit));
    }

    private void assertStdoutContains(String expected) {
        assertThat(stdio.getStdout(), containsString(expected));
    }

    /** The default errors from bad.js. */
    private List<String> expectedDefaultReportForBadJs(String path) {
        return ImmutableList.of(
                "jslint:" + path + ":1:1:'alert' was used before it was defined.",
                "jslint:" + path + ":1:10:Expected ';' and instead saw '(end)'.");
    }

    /** All because join() won't append a trailing newline. */
    private Iterable<String> maybeAddTrailer(List<String> lines) {
        if (lines.isEmpty()) {
            return lines;
        }
        return Iterables.concat(lines, ImmutableList.of(""));
    }

    private String pathTo(String js) throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(RESOURCE_PREFIX + js);
        return new File(resource.toURI()).getAbsolutePath();
    }

    /** Coerce arguments to an array of strings. */
    private int runLint(String... args) throws IOException {
        return main.run(args);
    }

    @Test
    public void testAllOk() throws IOException, URISyntaxException {
        int exit = runLint(pathTo("good.js"));
        assertLintOutput(exit, 0, NO_OUTPUT, NO_OUTPUT);
    }

    @Test
    public void testBadFlag() throws Exception {
        try {
            runLint("--xyzzy");
            fail("should have thrown DieException");
        } catch (DieException e) {
            // TODO: should exit with one not zero
            assertDiedHappily(e);
            assertThat(stdio.getStderr(), is(""));
            assertThat(stdio.getStdout(), containsString("Unknown option: --xyzzy"));
        }
    }

    @Test
    public void testBom() throws IOException, URISyntaxException {
        String path = pathTo("bom.js");
        int exit = runLint("--predef", "alert", path);
        assertLintOutput(exit, 0, NO_OUTPUT, NO_OUTPUT);
    }

    @Test
    public void testFileNotFound() throws Exception {
        File nonexistent = tempFolder.newFile("nonexistent.js");
        String path = nonexistent.getAbsolutePath();
        try {
            assertThat(nonexistent.delete(), is(true));
            runLint(path);
            fail("should have thrown DieException");
        } catch (DieException e) {
            assertDied(e, 1, is(path + ": No such file or directory."));
        }
    }

    @Test
    public void testHelp() throws Exception {
        try {
            runLint("--help");
            fail("should have thrown DieException");
        } catch (DieException e) {
            assertDiedHappily(e);
            assertStdoutContains("Usage: jslint4java [options] file.js ...");
            assertStdoutContains("--help");
            assertStdoutContains("using jslint version");
        }
    }

    @Test
    public void testHelpShownWhenNoFiles() throws Exception {
        try {
            runLint();
            fail("should have thrown DieException");
        } catch (DieException e) {
            assertDiedHappily(e);
            assertStdoutContains("Usage: jslint4java [options] file.js ...");
        }
    }

    @Test
    public void testMaybeGoodFile() throws IOException, URISyntaxException {
        // This test is mostly about interpreting flags correctly.
        int exit = runLint("--evil", "--sloppy", pathTo("maybe-good.js"));
        assertLintOutput(exit, 0, NO_OUTPUT, NO_OUTPUT);
    }

    @Test
    public void testOneBadFile() throws IOException, URISyntaxException {
        String path = pathTo("bad.js");
        int exit = runLint(path);
        assertLintOutput(exit, 1, expectedDefaultReportForBadJs(path), NO_OUTPUT);
    }

    /**
     * Does the default report look OK? This should be the same as not specifying --report.
     */
    @Test
    public void testReportDefault() throws IOException, URISyntaxException {
        String path = pathTo("bad.js");
        // TODO(hdm): Should be able to use "" rather than " ".
        int exit = runLint("--report", " ", path);
        assertLintOutput(exit, 1, expectedDefaultReportForBadJs(path), NO_OUTPUT);
    }

    /**
     * Does the plain report look OK?
     */
    @Test
    public void testReportPlain() throws IOException, URISyntaxException {
        String path = pathTo("bad.js");
        int exit = runLint("--report", "plain", path);
        List<String> expectedReport = ImmutableList.of(
                path + ":1:1: 'alert' was used before it was defined.",
                "alert(42)",
                "^",
                path + ":1:10: Expected ';' and instead saw '(end)'.",
                "alert(42)",
                "         ^",
                "");
        assertLintOutput(exit, 1, expectedReport, NO_OUTPUT);
    }

    /**
     * Complain if we ask for an unknown report.
     */
    @Test
    public void testReportUnknown() throws IOException, URISyntaxException {
        String path = pathTo("bad.js");
        try {
            runLint("--report", "UNKNOWN", path);
        } catch (DieException e) {
            assertDied(e, 1, is("unknown report type 'UNKNOWN'"));
        }
    }

    @Test
    public void testVersion() throws Exception {
        String edition = new JSLintBuilder().fromDefault().getEdition();
        try {
            runLint("--version");
            fail("should have thrown DieException");
        } catch (DieException e) {
            assertDiedHappily(e);
            List<String> expectedStdout = ImmutableList.of("using jslint version " + edition);
            assertLintOutput(e.getCode(), 0, expectedStdout, NO_OUTPUT);
        }
    }
}
