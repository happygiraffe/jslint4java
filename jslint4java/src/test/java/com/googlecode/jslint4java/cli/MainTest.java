package com.googlecode.jslint4java.cli;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import com.google.common.base.Joiner;

public class MainTest {

    /** Wrap stdio for testing. */
    public static class StdioResource extends ExternalResource {
        private static final String UTF_8 = "UTF-8";
        private InputStream origIn;
        private PrintStream origOut;
        private PrintStream origErr;
        private ByteArrayOutputStream stdoutStream;
        private ByteArrayOutputStream stderrStream;

        @Override
        protected void after() {
            if (origIn != null) {
                System.setIn(origIn);
            }
            System.setOut(origOut);
            System.setErr(origErr);
        }

        @Override
        protected void before() throws Throwable {
            captureStdout();
            captureStderr();
        }

        private void captureStderr() {
            origErr = System.err;
            stderrStream = new ByteArrayOutputStream();
            System.setErr(new PrintStream(stderrStream));
        }

        private void captureStdout() {
            origOut = System.out;
            stdoutStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(stdoutStream));
        }

        public String getStderr() {
            try {
                return stderrStream.toString(UTF_8);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e); // "should never happen"
            }
        }

        public String getStdout() {
            try {
                return stdoutStream.toString(UTF_8);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e); // "should never happen"
            }
        }

        public void setInput(String input) {
            origIn = System.in;
            System.setIn(new ByteArrayInputStream(input.getBytes(Charset.forName(UTF_8))));
        }
    }

    private static final String RESOURCE_PREFIX = "com/googlecode/jslint4java/";
    private static final List<String> NO_OUTPUT = new ArrayList<String>();

    @Rule
    public StdioResource stdio = new StdioResource();

    private final Main main = new Main();

    private void assertLintOutput(int actualExit, int expectedExit, List<String> expectedStdoutLines,
            List<String> expectedStderrLines) throws IOException, URISyntaxException {
        Joiner newlineJoiner = Joiner.on("\n");
        assertThat(stdio.getStdout(), is(newlineJoiner.join(expectedStdoutLines)));
        assertThat(stdio.getStderr(), is(newlineJoiner.join(expectedStderrLines)));
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
