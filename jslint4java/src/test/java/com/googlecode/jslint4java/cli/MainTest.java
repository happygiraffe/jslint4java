package com.googlecode.jslint4java.cli;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import com.google.common.base.Joiner;

public class MainTest {

    private static final String NEWLINE = System.getProperty("line.separator");
    private static final String RESOURCE_PREFIX = "com/googlecode/jslint4java/";
    private static final List<String> NO_OUTPUT = new ArrayList<String>();

    @Rule
    public StdioResource stdio = new StdioResource();

    private final Main main = new Main();

    private void assertLintOutput(int actualExit, int expectedExit, List<String> expectedStdoutLines,
            List<String> expectedStderrLines) throws IOException, URISyntaxException {
        Joiner nl = Joiner.on(NEWLINE);
        assertThat(stdio.getStdout(), is(nl.join(expectedStdoutLines)));
        assertThat(stdio.getStderr(), is(nl.join(expectedStderrLines)));
        // Do this last so that we see stdout/stderr errors first.
        assertThat(actualExit, is(expectedExit));
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
    public void testOneBad() throws IOException, URISyntaxException {
        String path = pathTo("bad.js");

        List<String> expectedStdout = Arrays.asList(
                "jslint:" + path + ":1:1:'alert' was used before it was defined.",
                "jslint:" + path + ":1:10:Expected ';' and instead saw '(end)'.",
                "");
        int exit = runLint(path);
        assertLintOutput(exit, 1, expectedStdout, NO_OUTPUT);
    }
}
