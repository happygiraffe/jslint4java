package net.happygiraffe.jslint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dom
 * @version $Id$
 */
public class Option {

    private static final String ADSAFE = "adsafe";

    private static final String BITWISE = "bitwise";

    private static final String BROWSER = "browser";

    private static final String CAP = "cap";

    private static final String DEBUG = "debug";

    private static final String EQEQEQ = "eqeqeq";

    private static final String EVIL = "evil";

    private static final String FRAGMENT = "fragment";

    private static final String LAXBREAK = "laxbreak";

    private static final String NOMEN = "nomen";

    private static final String PASSFAIL = "passfail";

    private static final String PLUSPLUS = "plusplus";

    private static final String RHINO = "rhino";

    private static final String UNDEF = "undef";

    private static final String WHITE = "white";

    private static final String WIDGET = "widget";

    private Map<String, Boolean> options = new HashMap<String, Boolean>();
    {
        options.put(ADSAFE, false);
        options.put(BITWISE, false);
        options.put(BROWSER, false);
        options.put(CAP, false);
        options.put(DEBUG, false);
        options.put(EQEQEQ, false);
        options.put(EVIL, false);
        options.put(FRAGMENT, false);
        options.put(LAXBREAK, false);
        options.put(NOMEN, false);
        options.put(PASSFAIL, false);
        options.put(PLUSPLUS, false);
        options.put(RHINO, false);
        options.put(UNDEF, false);
        options.put(WHITE, false);
        options.put(WIDGET, false);
    }

    public boolean isAdsafe() {
        return options.get(ADSAFE);
    }

    public boolean isEqeqeq() {
        return options.get(EQEQEQ);
    }

    public void setAdsafe(boolean adsafe) {
        options.put(ADSAFE, adsafe);
    }

    public void setEqeqeq(boolean eqeqeq) {
        options.put(EQEQEQ, eqeqeq);
    }

    private List<String> sortedOptionNames() {
        List<String> optionNames = new ArrayList<String>(options.keySet());
        Collections.sort(optionNames);
        return optionNames;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        // Use sorted list, so as to be more testable.
        for (String name : sortedOptionNames()) {
            if (options.get(name).booleanValue()) {
                sb.append(name + ":true");
                sb.append(",");
            }
        }
        if (sb.charAt(sb.length() - 1) == ',')
            sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }
}
