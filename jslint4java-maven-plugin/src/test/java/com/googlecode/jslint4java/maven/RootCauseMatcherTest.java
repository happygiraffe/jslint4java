package com.googlecode.jslint4java.maven;

import static com.googlecode.jslint4java.maven.RootCauseMatcher.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RootCauseMatcherTest {

    @Rule
    public ExpectedException kaboom = ExpectedException.none();

    private final RuntimeException exc = new RuntimeException(new IllegalArgumentException("foo"));

    @Test
    public void shouldMatchRootClauseByClass() {
        assertThat(exc, rootCause(IllegalArgumentException.class));
    }

    @Test
    public void shouldMatchRootClauseByMatcher() {
        assertThat(exc, rootCause(instanceOf(IllegalArgumentException.class)));
    }

    @Test
    public void shouldNotMatchNonException() {
        assertThat(new Object(), not(rootCause(IllegalArgumentException.class)));
    }

    @Test
    public void shouldDescribeFailure() {
        kaboom.handleAssertionErrors();
        kaboom.expect(AssertionError.class);
        kaboom.expectMessage(containsString("an exception with root cause"));

        // This should fail, and raise an AssertionErrror.
        assertThat(new Object(), rootCause(IllegalArgumentException.class));
    }
}
