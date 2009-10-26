package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class JSLintBuilderTest {

    // This jslint will return true for any file.
    private static final String STUB_JSLINT = "com/googlecode/jslint4java/stubjslint.js";

    private JSLintBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new JSLintBuilder();
    }

    @Test
    public void testFromDefault() throws Exception {
        JSLint lint = builder.fromDefault();
        assertThat(lint, is(notNullValue()));
        // Check it can lint OK.
        List<Issue> issues = lint.lint("-", "");
        assertThat(issues.isEmpty(), is(true));
    }

    @Test
    public void testFromClasspathResource() throws Exception {
        JSLint lint = builder.fromClasspathResource(STUB_JSLINT);
        assertThat(lint, is(notNullValue()));
        // Check it can lint OK.
        List<Issue> issues = lint.lint("-", "");
        assertThat(issues.isEmpty(), is(true));
    }

    @Test
    public void testFromFile() throws Exception {
        File f = new File(getClass().getClassLoader().getResource(STUB_JSLINT).toURI());
        JSLint lint = builder.fromFile(f);
        assertThat(lint, is(notNullValue()));
        // Check it can lint OK.
        List<Issue> issues = lint.lint("-", "");
        assertThat(issues.isEmpty(), is(true));
    }
}
