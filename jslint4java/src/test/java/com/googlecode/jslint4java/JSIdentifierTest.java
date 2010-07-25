package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class JSIdentifierTest {

    @Test
    public void testToString() {
        JSIdentifier id  = new JSIdentifier("foo", 42);
        assertThat(id.toString(), is("foo@42"));
    }

}
