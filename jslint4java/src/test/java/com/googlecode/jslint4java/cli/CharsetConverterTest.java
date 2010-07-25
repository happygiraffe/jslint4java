package com.googlecode.jslint4java.cli;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.junit.Test;

import com.beust.jcommander.ParameterException;

public class CharsetConverterTest {

    private static final String UNKNOWN_CHARSET_NAME = "bob";
    private final CharsetConverter cc = new CharsetConverter();

    @Test
    public void testConvert() throws Exception {
        assertThat(cc.convert("UTF-8"), is(Charset.forName("UTF-8")));
    }

    @Test(expected = ParameterException.class)
    public void testIllegalCharsetName() throws Exception {
        cc.convert("!@#$%^&*(");
    }

    @Test(expected = ParameterException.class)
    public void testUnknownCharset() throws Exception {
        // We only want to run this test if we can guarantee that the charset _doesn't_ exist in the
        // system first. Otherwise, we'll just have to ignore this test.
        try {
            Charset.forName(UNKNOWN_CHARSET_NAME);
        } catch (UnsupportedCharsetException e) {
            // We're good to run the test.
            cc.convert(UNKNOWN_CHARSET_NAME);
        }
    }

}
