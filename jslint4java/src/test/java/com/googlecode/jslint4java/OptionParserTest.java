package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class OptionParserTest {

    /** For generating exceptions. */
    private static class Foo {
        // Invoked via reflection.
        @SuppressWarnings("unused")
        public static Foo valueOf(String s) throws IOException {
            throw new IOException("burble");
        }
    }

    private final OptionParser optionParser = new OptionParser();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testNoSuchMethod() throws Exception {
        thrown.expect(RuntimeException.class);
        thrown.expectCause(CoreMatchers.<Throwable>instanceOf(NoSuchMethodException.class));
        thrown.expectMessage("System.valueOf");
        optionParser.parse(System.class, "foo");
    }

    @Test
    public void testParseBoolean() throws Exception {
        assertThat(optionParser.parse(Boolean.class, "true"), is(Boolean.TRUE));
    }

    @Test
    public void testParseInteger() throws Exception {
        assertThat(optionParser.parse(Integer.class, "2"), is(2));
    }

    @Test
    public void testParseIntegerFailure() throws Exception {
        thrown.expect(NumberFormatException.class);
        optionParser.parse(Integer.class, "foo");
    }

    @Test
    public void testRuntimeExceptionWrapping() throws Exception {
        thrown.expect(RuntimeException.class);
        thrown.expectCause(CoreMatchers.<Throwable>instanceOf(IOException.class));
        thrown.expectMessage("IOException: burble");
        optionParser.parse(Foo.class, "foo");
    }
}
