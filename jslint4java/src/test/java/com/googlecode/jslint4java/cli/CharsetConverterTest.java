package com.googlecode.jslint4java.cli;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assume.*;

import java.nio.charset.Charset;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.beust.jcommander.ParameterException;
import com.google.common.base.Charsets;

public class CharsetConverterTest {

    private static final String UNKNOWN_CHARSET_NAME = "bob";
    private final CharsetConverter cc = new CharsetConverter();

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConvert() throws Exception {
        assertThat(cc.convert("UTF-8"), is(Charsets.UTF_8));
    }

    @Test
    public void testIllegalCharsetName() throws Exception {
        thrown.expect(ParameterException.class);
        thrown.expectMessage("unknown encoding");
        cc.convert("!@#$%^&*(");
    }

    @Test
    public void testUnknownCharset() throws Exception {
        // We only want to run this test if we can guarantee that the charset _doesn't_ exist in the
        // system first. Otherwise, we'll just have to ignore this test.
        assumeThat(Charset.isSupported(UNKNOWN_CHARSET_NAME), is(false));

        // We're good to run the test.
        thrown.expect(ParameterException.class);
        thrown.expectMessage("unknown encoding");
        cc.convert(UNKNOWN_CHARSET_NAME);
    }

}
