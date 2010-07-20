package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class OptionParserTest {

    private final OptionParser optionParser = new OptionParser();

    @Test
    public void testParseBoolean() throws Exception {
        assertThat(optionParser.parse(Boolean.class, "true"), is(Boolean.TRUE));
    }

    @Test
    public void testParseInteger() throws Exception {
        assertThat(optionParser.parse(Integer.class, "2"), is(2));
    }

    @Test(expected = NumberFormatException.class)
    public void testParseIntegerFailure() throws Exception {
        optionParser.parse(Integer.class, "foo");
    }

    @Test(expected = RuntimeException.class)
    public void testNoSuchMethod() throws Exception {
        optionParser.parse(System.class, "foo");
    }
}
