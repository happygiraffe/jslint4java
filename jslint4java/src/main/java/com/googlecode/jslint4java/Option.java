package com.googlecode.jslint4java;

import java.util.Locale;

/**
 * All available options for tuning the behaviour of JSLint.
 *
 * TODO Add a "Handler" class for each type, which knows whether it needs an
 * arg, how to parse it, etc.
 *
 * @author dom
 */
public enum Option {
    // BEGIN-OPTIONS
    /** If adsafe should be enforced */
    ADSAFE("If adsafe should be enforced", Boolean.class),

    /** If bitwise operators should not be allowed */
    BITWISE("If bitwise operators should not be allowed", Boolean.class),

    /** If the standard browser globals should be predefined */
    BROWSER("If the standard browser globals should be predefined", Boolean.class),

    /** If upper case html should be allowed */
    CAP("If upper case html should be allowed", Boolean.class),

    /** If css workarounds should be tolerated */
    CSS("If css workarounds should be tolerated", Boolean.class),

    /** If debugger statements should be allowed */
    DEBUG("If debugger statements should be allowed", Boolean.class),

    /** If logging should be allowed (console, alert, etc.) */
    DEVEL("If logging should be allowed (console, alert, etc.)", Boolean.class),

    /** If === should be required */
    EQEQEQ("If === should be required", Boolean.class),

    /** If es5 syntax should be allowed */
    ES5("If es5 syntax should be allowed", Boolean.class),

    /** If eval should be allowed */
    EVIL("If eval should be allowed", Boolean.class),

    /** If for in statements must filter */
    FORIN("If for in statements must filter", Boolean.class),

    /** If html fragments should be allowed */
    FRAGMENT("If html fragments should be allowed", Boolean.class),

    /** If immediate invocations must be wrapped in parens */
    IMMED("If immediate invocations must be wrapped in parens", Boolean.class),

    /** The number of spaces used for indentation (default is 4) */
    INDENT("The number of spaces used for indentation (default is 4)", Integer.class),

    /** If line breaks should not be checked */
    LAXBREAK("If line breaks should not be checked", Boolean.class),

    /** The maximum number of warnings reported (default is 50) */
    MAXERR("The maximum number of warnings reported (default is 50)", Integer.class),

    /** Maximum line length */
    MAXLEN("Maximum line length", Integer.class),

    /** If constructor names must be capitalized */
    NEWCAP("If constructor names must be capitalized", Boolean.class),

    /** If names should be checked */
    NOMEN("If names should be checked", Boolean.class),

    /** If html event handlers should be allowed */
    ON("If html event handlers should be allowed", Boolean.class),

    /** If only one var statement per function should be allowed */
    ONEVAR("If only one var statement per function should be allowed", Boolean.class),

    /** If the scan should stop on first error */
    PASSFAIL("If the scan should stop on first error", Boolean.class),

    /** If increment/decrement should not be allowed */
    PLUSPLUS("If increment/decrement should not be allowed", Boolean.class),

    /** The names of predefined global variables. */
    PREDEF("The names of predefined global variables.", StringArray.class),

    /** If the . should not be allowed in regexp literals */
    REGEXP("If the . should not be allowed in regexp literals", Boolean.class),

    /** If the rhino environment globals should be predefined */
    RHINO("If the rhino environment globals should be predefined", Boolean.class),

    /** If use of some browser features should be restricted */
    SAFE("If use of some browser features should be restricted", Boolean.class),

    /** Require the "use strict"; pragma */
    STRICT("Require the \"use strict\"; pragma", Boolean.class),

    /** If all forms of subscript notation are tolerated */
    SUB("If all forms of subscript notation are tolerated", Boolean.class),

    /** If variables should be declared before used */
    UNDEF("If variables should be declared before used", Boolean.class),

    /** If strict whitespace rules apply */
    WHITE("If strict whitespace rules apply", Boolean.class),

    /** If the yahoo widgets globals should be predefined */
    WIDGET("If the yahoo widgets globals should be predefined", Boolean.class),

    /** If ms windows-specigic globals should be predefined */
    WINDOWS("If ms windows-specigic globals should be predefined", Boolean.class),

    // END-OPTIONS
    ;

    private String description;
    private Class<?> type;

    private Option(String description, Class<?> type) {
        this.description = description;
        this.type = type;
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
     * What type does the value of this option have?
     */
    public Class<?> getType() {
        return type;
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
