package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class JSLintBuilderTest {

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
        // This will validate anything as passing…
        String resource = "com/googlecode/jslint4java/stubjslint.js";
        JSLint lint = builder.fromClasspathResource(resource);
        assertThat(lint, is(notNullValue()));
        // Check it can lint OK.
        List<Issue> issues = lint.lint("-", "");
        assertThat(issues.isEmpty(), is(true));
    }
}
