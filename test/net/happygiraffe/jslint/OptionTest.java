package net.happygiraffe.jslint;

import junit.framework.TestCase;

/**
 * @author dom
 * @version $Id$
 */
public class OptionTest extends TestCase {

    public void testGetDescription() {
        assertEquals("If eval should be allowed", Option.EVIL.getDescription());
    }
    
    public void testGetLowerName() {
        assertEquals("evil", Option.EVIL.getLowerName());
    }
    
    public void testToString() throws Exception {
        assertEquals("evil[If eval should be allowed]", Option.EVIL.toString());
    }
    
    // This is useful for formatting lists of options...
    public void testMaximumNameLength() throws Exception {
        assertEquals(8, Option.maximumNameLength());
    }
    
}
