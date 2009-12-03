package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class StringArrayTest {

    @Test
    public void testValueOf() {
        String[] ary = StringArray.valueOf("a,b");
        assertThat(ary.length, is(2));
        assertThat(ary[0], is("a"));
        assertThat(ary[1], is("b"));
    }

}
