package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;

public class JSLintBuilderTest {

    // This jslint will return true for any file.
    private static final String STUB_JSLINT = "com/googlecode/jslint4java/stubjslint.js";

    private final JSLintBuilder builder = new JSLintBuilder();

    /**
     * A minimal test that a JSLint looks OK. Just tests we can lint an empty
     * string.
     */
    private void assertJSLintOK(JSLint lint) {
        assertThat(lint, is(notNullValue()));
        // Check it can lint OK.
        List<Issue> issues = lint.lint("-", "").getIssues();
        assertThat(issues.isEmpty(), is(true));
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
        File f = new File(getClass().getClassLoader().getResource(STUB_JSLINT).toURI());
        assertJSLintOK(builder.fromFile(f));
    }

    @Test
    public void testFromReader() throws Exception {
        // Same as stubjslint.js.
        String jslint = "function JSLINT() {JSLINT.errors=[];return true}";
        StringReader reader = new StringReader(jslint);
        assertJSLintOK(builder.fromReader(reader, "stubjslint.js"));
    }

    @Test
    public void canBuildMultipleLintersFromSameInstance() throws Exception {
        assertJSLintOK(builder.fromDefault());
        // Making another linter from the same builder should be fine.
        assertJSLintOK(builder.fromDefault());
    }
}
