package net.happygiraffe.jslint;

import java.util.Locale;

/**
 * All available options for tuning the behaviour of JSLint.
 *
 * TODO Add a "Handler" class for each type, which knows whether it needs an
 * arg, how to parse it, etc.
 *
 * @author dom
 * @version $Id$
 */
public enum Option {
    //BEGIN-OPTIONS
    /** If adsafe should be enforced */
    ADSAFE("If adsafe should be enforced"),
    /** If bitwise operators should not be allowed */
    BITWISE("If bitwise operators should not be allowed"),
    /** If the standard browser globals should be predefined */
    BROWSER("If the standard browser globals should be predefined"),
    /** If upper case html should be allowed */
    CAP("If upper case html should be allowed"),
    /** If css workarounds should be tolerated */
    CSS("If css workarounds should be tolerated"),
    /** If debugger statements should be allowed */
    DEBUG("If debugger statements should be allowed"),
    /** If === should be required */
    EQEQEQ("If === should be required"),
    /** If eval should be allowed */
    EVIL("If eval should be allowed"),
    /** If for in statements must filter */
    FORIN("If for in statements must filter"),
    /** If html fragments should be allowed */
    FRAGMENT("If html fragments should be allowed"),
    /** If immediate invocations must be wrapped in parens */
    IMMED("If immediate invocations must be wrapped in parens"),
    /** If line breaks should not be checked */
    LAXBREAK("If line breaks should not be checked"),
    /** If constructor names must be capitalized */
    NEWCAP("If constructor names must be capitalized"),
    /** If names should be checked */
    NOMEN("If names should be checked"),
    /** If html event handlers should be allowed */
    ON("If html event handlers should be allowed"),
    /** If only one var statement per function should be allowed */
    ONEVAR("If only one var statement per function should be allowed"),
    /** If the scan should stop on first error */
    PASSFAIL("If the scan should stop on first error"),
    /** If increment/decrement should not be allowed */
    PLUSPLUS("If increment/decrement should not be allowed"),
    /** If the . should not be allowed in regexp literals */
    REGEXP("If the . should not be allowed in regexp literals"),
    /** If the rhino environment globals should be predefined */
    RHINO("If the rhino environment globals should be predefined"),
    /** If use of some browser features should be restricted */
    SAFE("If use of some browser features should be restricted"),
    /** If the system object should be predefined */
    SIDEBAR("If the system object should be predefined"),
    /** Require the "use strict"; pragma */
    STRICT("Require the \"use strict\"; pragma"),
    /** If all forms of subscript notation are tolerated */
    SUB("If all forms of subscript notation are tolerated"),
    /** If variables should be declared before used */
    UNDEF("If variables should be declared before used"),
    /** If strict whitespace rules apply */
    WHITE("If strict whitespace rules apply"),
    /** If the yahoo widgets globals should be predefined */
    WIDGET("If the yahoo widgets globals should be predefined");
    //END-OPTIONS

    private String description;

    private Option(String description) {
        this.description = description;
    }

    /**
     * Return a description of what this option affects.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the lowercase name of this option.
     */
    public String getLowerName() {
        return name().toLowerCase(Locale.getDefault());
    }

    /**
     * Calculate the maximum length of all of the {@link Option} names.
     *
     * @return the length of the largest name.
     */
    public static int maximumNameLength() {
        int maxOptLen = 0;
        for (Option o : values()) {
            int len = o.name().length();
            if (len > maxOptLen) {
                maxOptLen = len;
            }
        }
        return maxOptLen;
    }

    /**
     * Show this option and its description.
     */
    @Override
    public String toString() {
        return getLowerName() + "[" + getDescription() + "]";
    }
}
