package com.googlecode.jslint4java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptableObject;

/**
 * Construct {@link JSLint} instances.
 *
 * @author hdm
 */
public class JSLintBuilder {
    private static final String JSLINT_FILE = "com/googlecode/jslint4java/jslint.js";

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private ContextFactory contextFactory = new ContextFactory();

    /**
     * Initialize the scope from a jslint.js found in the classpath. Assumes a
     * UTF-8 encoding.
     *
     * @param resource
     *            the location of jslint.js on the classpath.
     * @return a configured {@link JSLint}
     * @throws IOException
     *             if there are any problems reading the resource.
     */
    public JSLint fromClasspathResource(String resource) throws IOException {
        return fromClasspathResource(resource, UTF8);
    }

    /**
     * Initialize the scope from a jslint.js found in the classpath.
     *
     * @param resource
     *            the location of jslint.js on the classpath.
     * @param encoding
     *            the encoding of the resource.
     * @return a configured {@link JSLint}
     * @throws IOException
     *             if there are any problems reading the resource.
     */
    public JSLint fromClasspathResource(String resource, Charset encoding) throws IOException {
        Reader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader()
                .getResourceAsStream(resource), encoding));
        return fromReader(reader, resource);
    }

    /**
     * Initialize the scope with a default jslint.js.
     *
     * @return a configured {@link JSLint}
     * @throws RuntimeException
     *             if we fail to load the default jslint.js.
     */
    public JSLint fromDefault() {
        try {
            return fromClasspathResource(JSLINT_FILE);
        } catch (IOException e) {
            // We wrap and rethrow, as there's nothing a caller can do in this
            // case.
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialize the scope with the jslint.js passed in on the filesystem.
     * Assumes a UTF-8 encoding.
     *
     * @param f
     *            the path to jslint.js
     * @return a configured {@link JSLint}
     * @throws IOException
     *             if the file can't be read.
     */
    public JSLint fromFile(File f) throws IOException {
        return fromFile(f, UTF8);
    }

    /**
     * Initialize the scope with the jslint.js passed in on the filesystem.
     *
     * @param f
     *            the path to jslint.js
     * @param encoding
     *            the encoding of the file
     * @return a configured {@link JSLint}
     * @throws IOException
     *             if the file can't be read.
     */
    public JSLint fromFile(File f, Charset encoding) throws IOException {
        Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), encoding));
        return fromReader(reader, f.toString());
    }

    /**
     * Initialize the scope with an arbitrary jslint.
     *
     * @param reader
     *            an input source providing jslint.js.
     * @param name
     *            the name of the resource backed by the reader
     * @return a configured {@link JSLint}
     * @throws IOException
     *             if there are any problems reading from {@code reader} .
     */
    @NeedsContext
    public JSLint fromReader(Reader reader, String name) throws IOException {
        try {
            Context cx = contextFactory.enterContext();
            ScriptableObject scope = cx.initStandardObjects();
            cx.evaluateReader(scope, reader, name, 1, null);
            return new JSLint(contextFactory, scope);
        } finally {
            Context.exit();
        }
    }

    /**
     * Set this JSLint instance to time out after maxTimeInSeconds.
     *
     * @param maxTimeInSeconds
     *            maximum execution time in seconds.
     * @return this
     */
    public JSLintBuilder timeout(long maxTimeInSeconds) {
        return timeout(maxTimeInSeconds, TimeUnit.SECONDS);
    }

    /**
     * Set this JSLint instance to timeout after maxTime.
     *
     * @param maxTime
     *            The maximum execution time.
     * @param timeUnit
     *            The unit of maxTime.
     * @return this
     */
    public JSLintBuilder timeout(long maxTime, TimeUnit timeUnit) {
        contextFactory = new TimeLimitedContextFactory(maxTime, timeUnit);
        return this;
    }
}