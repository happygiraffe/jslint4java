package net.happygiraffe.jslint;

import junit.framework.TestCase;

/**
 * @author dom
 * @version $Id$
 */
public class OptionTest extends TestCase {

    public void testToStringWithNoOptionsSet() throws Exception {
        Option o = new Option();
        assertEquals("{}", o.toString());
    }
    
    public void testToStringWithOneOptionSet() throws Exception {
        Option o = new Option();
        o.setEqeqeq(true);
        assertEquals("{eqeqeq:true}", o.toString());
    }
    
    public void testToStringWithTwoOptionsSet() throws Exception {
        Option o = new Option();
        o.setEqeqeq(true);
        o.setAdsafe(true);
        assertEquals("{adsafe:true,eqeqeq:true}", o.toString());
    }

}
