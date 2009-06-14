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
    ADSAFE("If adsafe should be enforced", false),
    /** If bitwise operators should not be allowed */
    BITWISE("If bitwise operators should not be allowed", false),
    /** If the standard browser globals should be predefined */
    BROWSER("If the standard browser globals should be predefined", false),
    /** If upper case html should be allowed */
    CAP("If upper case html should be allowed", false),
    /** If css workarounds should be tolerated */
    CSS("If css workarounds should be tolerated", false),
    /** If debugger statements should be allowed */
    DEBUG("If debugger statements should be allowed", false),
    /** If === should be required */
    EQEQEQ("If === should be required", false),
    /** If eval should be allowed */
    EVIL("If eval should be allowed", false),
    /** If for in statements must filter */
    FORIN("If for in statements must filter", false),
    /** If html fragments should be allowed */
    FRAGMENT("If html fragments should be allowed", false),
    /** If immediate invocations must be wrapped in parens */
    IMMED("If immediate invocations must be wrapped in parens", false),
    /** The number of spaces used for indentation (default is 4) */
    INDENT("The number of spaces used for indentation (default is 4)", true),
    /** If line breaks should not be checked */
    LAXBREAK("If line breaks should not be checked", false),
    /** If constructor names must be capitalized */
    NEWCAP("If constructor names must be capitalized", false),
    /** If names should be checked */
    NOMEN("If names should be checked", false),
    /** If html event handlers should be allowed */
    ON("If html event handlers should be allowed", false),
    /** If only one var statement per function should be allowed */
    ONEVAR("If only one var statement per function should be allowed", false),
    /** If the scan should stop on first error */
    PASSFAIL("If the scan should stop on first error", false),
    /** If increment/decrement should not be allowed */
    PLUSPLUS("If increment/decrement should not be allowed", false),
    /** If the . should not be allowed in regexp literals */
    REGEXP("If the . should not be allowed in regexp literals", false),
    /** If the rhino environment globals should be predefined */
    RHINO("If the rhino environment globals should be predefined", false),
    /** If use of some browser features should be restricted */
    SAFE("If use of some browser features should be restricted", false),
    /** If the system object should be predefined */
    SIDEBAR("If the system object should be predefined", false),
    /** Require the "use strict"; pragma */
    STRICT("Require the \"use strict\"; pragma", false),
    /** If all forms of subscript notation are tolerated */
    SUB("If all forms of subscript notation are tolerated", false),
    /** If variables should be declared before used */
    UNDEF("If variables should be declared before used", false),
    /** If strict whitespace rules apply */
    WHITE("If strict whitespace rules apply", false),
    /** If the yahoo widgets globals should be predefined */
    WIDGET("If the yahoo widgets globals should be predefined", false);
    //END-OPTIONS

    private String description;
    private boolean hasArgument;

    private Option(String description, boolean hasArgument) {
        this.description = description;
        this.hasArgument = hasArgument;
    }

    // Default to "no argument".
    private Option(String description) {
        this(description, false);
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

    public boolean hasArgument() {
        return hasArgument;
    }
}
