package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

public class UnicodeBomInputStreamTest {

    @Test
    public void basicSanity() throws Exception {
        // UTF-8 BOM + "12345"
        ByteArrayInputStream in = new ByteArrayInputStream(new byte[] { (byte) 0xEF, (byte) 0xBB,
                (byte) 0xBF, 0x31, 0x32, 0x33, 0x34, 0x35 });
        UnicodeBomInputStream in2 = new UnicodeBomInputStream(in);
        try {
            in2.skipBOM();
            assertThat(in2.read(), is(0x31));
        } finally {
            IOUtils.closeQuietly(in2);
        }
    }

    @Test
    public void basicSanityFromResource() throws Exception {
        InputStream is = UnicodeBomInputStreamTest.class.getResourceAsStream("bom.js");
        UnicodeBomInputStream is2 = new UnicodeBomInputStream(is);
        try {
            is2.skipBOM();
            // Should start with a double slash then space.
            assertThat(is2.read(), is(0x2f));
            assertThat(is2.read(), is(0x2f));
            assertThat(is2.read(), is(0x20));
        } finally {
            IOUtils.closeQuietly(is2);
        }
    }

    @Test
    public void basicSanityFromResourceReader() throws Exception {
        UnicodeBomInputStream is2 = new UnicodeBomInputStream(getBomJs());
        is2.skipBOM();
        String s = CharStreams.toString(new InputStreamReader(is2, Charsets.UTF_8));
        String nl = System.getProperty("line.separator");
        assertThat(s, is("// This file starts with a UTF-8 BOM." + nl + "alert(\"Hello BOM\");" + nl));
    }

    @Test
    public void canLintWithBom() throws Exception {
        UnicodeBomInputStream is2 = new UnicodeBomInputStream(getBomJs());
        is2.skipBOM();
        InputStreamReader isr = new InputStreamReader(is2, Charsets.UTF_8);
        JSLint jsLint = new JSLintBuilder().fromDefault();
        jsLint.addOption(Option.PREDEF, "alert");
        JSLintResult result = jsLint.lint("bom.js", isr);
        assertThat(result.getIssues(), empty());
    }

    private InputStream getBomJs() throws IOException {
        URL url = Resources.getResource(UnicodeBomInputStreamTest.class, "bom.js");
        return Resources.asCharSource(url, Charsets.UTF_8).asByteSource(Charsets.UTF_8).openStream();
    }
}
