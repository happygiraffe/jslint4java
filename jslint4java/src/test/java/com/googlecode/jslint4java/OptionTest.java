package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/**
 * @author dom
 */
public class OptionTest {

    @Test
    public void testGetDescription() {
        assertThat(Option.EVIL.getDescription(), is("If eval should be allowed"));
    }

    @Test
    public void testGetLowerName() {
        assertThat(Option.EVIL.getLowerName(), is("evil"));
    }

    @Test
    public void testToString() throws Exception {
        assertThat(Option.EVIL.toString(), is("evil[If eval should be allowed]"));
    }

    // This is useful for formatting lists of options...
    @Test
    public void testMaximumNameLength() throws Exception {
        assertThat(Option.maximumNameLength(), is(10));
    }

}
