package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class JSFunctionTest {

    @Test
    public void testToString() {
        JSFunction f = new JSFunction.Builder("fred", 1).build();
        assertThat(f.toString(), is("function fred()"));
    }

}
