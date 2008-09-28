package net.happygiraffe.jslint;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class OptionBindingTest {

    @Test
    public void testToString() {
        OptionBinding ob = new OptionBinding(Option.EVIL, true);
        assertThat(ob.toString(), is("evil=true"));
    }

}
