package com.googlecode.jslint4java.cli;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.rules.ExternalResource;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;

/**
 * A JUnit @Rule to wrap stdio for testing.
 */
public class StdioResource extends ExternalResource {
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
            throw Throwables.propagate(e); // "should never happen"
        }
    }

    public String getStdout() {
        try {
            return stdoutStream.toString(UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw Throwables.propagate(e); // "should never happen"
        }
    }

    public void setInput(String input) {
        origIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes(Charsets.UTF_8)));
    }
}