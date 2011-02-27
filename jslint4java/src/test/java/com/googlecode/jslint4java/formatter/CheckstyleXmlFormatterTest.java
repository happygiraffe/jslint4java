package com.googlecode.jslint4java.formatter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class CheckstyleXmlFormatterTest {

    private final JSLintResultFormatter form = new CheckstyleXmlFormatter();

    @Test
    public void shouldHaveCheckstyleFooter() {
        assertThat(form.footer(), is("</checkstyle>"));
    }

    @Test
    public void shouldHaveCheckstyleHeader() {
        assertThat(form.header(), is("<checkstyle>"));
    }

}
