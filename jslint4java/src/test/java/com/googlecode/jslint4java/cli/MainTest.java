package com.googlecode.jslint4java.cli;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

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

    private final Main main = new Main();

    /** Check that the exit value, stdout and stderr are as expected. */
    private void assertLintOutput(int actualExit, int expectedExit, List<String> expectedStdout,
            List<String> expectedStderr) {
        Joiner nl = Joiner.on(NEWLINE);
        assertThat(stdio.getStdout(), is(nl.join(maybeAddTrailer(expectedStdout))));
        assertThat(stdio.getStderr(), is(nl.join(maybeAddTrailer(expectedStderr))));
        // Do this last so that we see stdout/stderr errors first.
        assertThat(actualExit, is(expectedExit));
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
    public void testBom() throws IOException, URISyntaxException {
        String path = pathTo("bom.js");
        int exit = runLint("--predef", "alert", path);
        assertLintOutput(exit, 0, NO_OUTPUT, NO_OUTPUT);
    }

    @Test
    public void testOneBad() throws IOException, URISyntaxException {
        String path = pathTo("bad.js");

        List<String> expectedStdout = ImmutableList.of(
                "jslint:" + path + ":1:1:'alert' was used before it was defined.",
                "jslint:" + path + ":1:10:Expected ';' and instead saw '(end)'.");
        int exit = runLint(path);
        assertLintOutput(exit, 1, expectedStdout, NO_OUTPUT);
    }

    @Test
    public void testVersion() throws Exception {
        String edition = new JSLintBuilder().fromDefault().getEdition();
        try {
          runLint("--version");
          fail("should have thrown DieException");
        } catch (DieException e) {
            List<String> expectedStdout = ImmutableList.of("using jslint version " + edition);
            assertLintOutput(e.getCode(), 0, expectedStdout, NO_OUTPUT);
        }
    }
}
