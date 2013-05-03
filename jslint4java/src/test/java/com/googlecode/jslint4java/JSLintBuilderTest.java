package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class JSLintBuilderTest {

    // This jslint will return true for any file.
    private static final String STUB_JSLINT = "com/googlecode/jslint4java/stubjslint.js";

    private final JSLintBuilder builder = new JSLintBuilder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * A minimal test that a JSLint looks OK. Just tests we can lint an empty
     * string.
     */
    private void assertJSLintOK(JSLint lint) {
        assertThat(lint, is(notNullValue()));
        // Check it can lint OK.
        JSLintResult result = lint.lint("-", "");
        assertThat(result.getIssues(), empty());
    }

    /** Return a stubbed out jslint.js from the classpath. */
    private URL getStubJslint() {
        return Resources.getResource(STUB_JSLINT);
    }

    @Test
    public void testFromClasspathResource() throws Exception {
        assertJSLintOK(builder.fromClasspathResource(STUB_JSLINT));
    }

    @Test
    public void testFromDefault() throws Exception {
        assertJSLintOK(builder.fromDefault());
    }

    @Test
    public void testFromFile() throws Exception {
        File f = new File(getStubJslint().toURI());
        assertJSLintOK(builder.fromFile(f));
    }

    @Test
    public void testFromReader() throws Exception {
        InputStreamReader input = Resources.newReaderSupplier(getStubJslint(), Charsets.UTF_8)
                .getInput();
        assertJSLintOK(builder.fromReader(input, "stubjslint.js"));
    }

    @Test
    public void canBuildMultipleLintersFromSameInstance() throws Exception {
        assertJSLintOK(builder.fromDefault());
        // Making another linter from the same builder should be fine.
        assertJSLintOK(builder.fromDefault());
    }

    @Test
    public void timeOutThrowsException() {
        thrown.expect(TimeLimitedContextFactory.TimeExceededException.class);
        // Hopefully 0ns should be short enough to trigger a timeout. I'm
        // betting that we have enough ops in parsing JSLint to trigger the
        // context factory's periodic checks.
        JSLint lint = builder.timeout(0, TimeUnit.NANOSECONDS).fromDefault();
        lint.lint("-", "alert(42)");
    }
}
