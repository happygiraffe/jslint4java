package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.junit.Test;

public class UnicodeBomInputStreamTest {

    @Test
    public void basicSanity() throws Exception {
        // UTF-8 BOM + "12345"
        ByteArrayInputStream in = new ByteArrayInputStream(new byte[] { (byte) 0xEF, (byte) 0xBB,
                (byte) 0xBF, 0x31, 0x32, 0x33, 0x34, 0x35 });
        UnicodeBomInputStream in2 = new UnicodeBomInputStream(in);
        in2.skipBOM();
        assertThat(in2.read(), is(0x31));
    }

    @Test
    public void basicSanityFromResource() throws Exception {
        InputStream is = UnicodeBomInputStreamTest.class.getResourceAsStream("bom.js");
        UnicodeBomInputStream is2 = new UnicodeBomInputStream(is);
        is2.skipBOM();
        // Should start with a double slash then space.
        assertThat(is2.read(), is(0x2f));
        assertThat(is2.read(), is(0x2f));
        assertThat(is2.read(), is(0x20));
    }

    @Test
    public void basicSanityFromResourceReader() throws Exception {
        InputStream is = UnicodeBomInputStreamTest.class.getResourceAsStream("bom.js");
        UnicodeBomInputStream is2 = new UnicodeBomInputStream(is);
        is2.skipBOM();
        InputStreamReader isr = new InputStreamReader(is2, Charset.forName("UTF-8"));
        String s = Util.readerToString(isr);
        assertThat(s, is("// This file starts with a UTF-8 BOM.\nalert(\"Hello BOM\");\n"));
    }

    @Test
    public void canLintWithBom() throws Exception {
        InputStream is = UnicodeBomInputStreamTest.class.getResourceAsStream("bom.js");
        UnicodeBomInputStream is2 = new UnicodeBomInputStream(is);
        is2.skipBOM();
        InputStreamReader isr = new InputStreamReader(is2, Charset.forName("UTF-8"));
        JSLint jsLint = new JSLintBuilder().fromDefault();
        JSLintResult result = jsLint.lint("bom.js", isr);
        assertThat(result.getIssues().size(), is(0));
    }
}
