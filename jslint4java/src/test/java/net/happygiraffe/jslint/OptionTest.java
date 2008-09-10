package net.happygiraffe.jslint;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dom
 * @version $Id$
 */
public class OptionTest {

    @Test
    public void testGetDescription() {
        assertEquals("If eval should be allowed", Option.EVIL.getDescription());
    }

    @Test
    public void testGetLowerName() {
        assertEquals("evil", Option.EVIL.getLowerName());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("evil[If eval should be allowed]", Option.EVIL.toString());
    }

    // This is useful for formatting lists of options...
    @Test
    public void testMaximumNameLength() throws Exception {
        assertEquals(8, Option.maximumNameLength());
    }

}
