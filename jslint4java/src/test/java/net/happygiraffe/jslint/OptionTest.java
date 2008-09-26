package net.happygiraffe.jslint;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dom
 * @version $Id$
 */
public class OptionTest {

    @Test
    public void testGetDescription() {
        assertThat(Option.EVIL.getDescription(),
                is("If eval should be allowed"));
    }

    @Test
    public void testGetType() throws Exception {
        Class<?> type = Option.EVIL.getType();
        assertThat(type, is(Class.class));
        assertThat(type.getName(),
                is("net.happygiraffe.jslint.BooleanOptionInstance"));
    }

    @Test
    public void testGetInstance() throws Exception {
        OptionInstance oi = Option.EVIL.getInstance();
        assertThat(oi, is(notNullValue()));
        assertThat(oi.getOption(), is(Option.EVIL));
        assertThat(oi.getValue(), is((Object)true));
    }

    @Test
    public void testGetLowerName() {
        assertThat(Option.EVIL.getLowerName(), is("evil"));
    }

    @Test
    public void testToString() throws Exception {
        assertThat(Option.EVIL.toString(),
                is("evil[If eval should be allowed]"));
    }

    // This is useful for formatting lists of options...
    @Test
    public void testMaximumNameLength() throws Exception {
        assertThat(Option.maximumNameLength(), is(8));
    }

}
