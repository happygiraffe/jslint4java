package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.googlecode.jslint4java.OptionParser;


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
}
