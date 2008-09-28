package net.happygiraffe.jslint;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class OptionBinderTest {

    /**
     * This is a special case hack. The mere presence of a boolean option should
     * cause it to return true, so the normal {@link Boolean#valueOf(String)}
     * rules are waived in this instance.
     */
    @Test
    public void testBooleanParseNull() throws Exception {
        OptionBinding ob = OptionBinder.BOOLEAN.bind(Option.ADSAFE, null);
        assertThat(ob.getValue(), is((Object) Boolean.TRUE));
    }

    @Test
    public void testBooleanParseFalse() throws Exception {
        OptionBinding ob = OptionBinder.BOOLEAN.bind(Option.ADSAFE, "false");
        assertThat(ob.getValue(), is((Object) Boolean.FALSE));
    }

    @Test
    public void testBooleanParseTrue() throws Exception {
        OptionBinding ob = OptionBinder.BOOLEAN.bind(Option.ADSAFE, "true");
        assertThat(ob.getValue(), is((Object) Boolean.TRUE));
    }
}
