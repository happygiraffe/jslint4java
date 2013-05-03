package com.googlecode.jslint4java;

import static org.hamcrest.Matchers.arrayContaining;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class StringArrayTest {

    @Test
    public void testValueOf() {
        String[] ary = StringArray.valueOf("a,b");
        assertThat(ary, arrayContaining("a", "b"));
    }

}
