package net.happygiraffe.jslint;

import java.util.Locale;

/**
 * All available options for tuning the behaviour of JSLint.
 *
 * @author dom
 * @version $Id$
 */
public enum Option {
    INDENT("How many spaces to indent by", int.class),
    //BEGIN-OPTIONS
    /** If adsafe should be enforced */
    ADSAFE("If adsafe should be enforced", boolean.class),
    /** If bitwise operators should not be allowed */
    BITWISE("If bitwise operators should not be allowed", boolean.class),
    /** If the standard browser globals should be predefined */
    BROWSER("If the standard browser globals should be predefined", boolean.class),
    /** If upper case html should be allowed */
    CAP("If upper case html should be allowed", boolean.class),
    /** If css workarounds should be tolerated */
    CSS("If css workarounds should be tolerated", boolean.class),
    /** If debugger statements should be allowed */
    DEBUG("If debugger statements should be allowed", boolean.class),
    /** If === should be required */
    EQEQEQ("If === should be required", boolean.class),
    /** If eval should be allowed */
    EVIL("If eval should be allowed", boolean.class),
    /** If for in statements must filter */
    FORIN("If for in statements must filter", boolean.class),
    /** If html fragments should be allowed */
    FRAGMENT("If html fragments should be allowed", boolean.class),
    /** If line breaks should not be checked */
    LAXBREAK("If line breaks should not be checked", boolean.class),
    /** If names should be checked */
    NOMEN("If names should be checked", boolean.class),
    /** If html event handlers should be allowed */
    ON("If html event handlers should be allowed", boolean.class),
    /** If only one var statement per function should be allowed */
    ONEVAR("If only one var statement per function should be allowed", boolean.class),
    /** If the scan should stop on first error */
    PASSFAIL("If the scan should stop on first error", boolean.class),
    /** If increment/decrement should not be allowed */
    PLUSPLUS("If increment/decrement should not be allowed", boolean.class),
    /** If the . should not be allowed in regexp literals */
    REGEXP("If the . should not be allowed in regexp literals", boolean.class),
    /** If the rhino environment globals should be predefined */
    RHINO("If the rhino environment globals should be predefined", boolean.class),
    /** If use of some browser features should be restricted */
    SAFE("If use of some browser features should be restricted", boolean.class),
    /** If the system object should be predefined */
    SIDEBAR("If the system object should be predefined", boolean.class),
    /** Require the "use strict"; pragma */
    STRICT("Require the \"use strict\"; pragma", boolean.class),
    /** If all forms of subscript notation are tolerated */
    SUB("If all forms of subscript notation are tolerated", boolean.class),
    /** If variables should be declared before used */
    UNDEF("If variables should be declared before used", boolean.class),
    /** If strict whitespace rules apply */
    WHITE("If strict whitespace rules apply", boolean.class),
    /** If the yahoo widgets globals should be predefined */
    WIDGET("If the yahoo widgets globals should be predefined", boolean.class);
    //END-OPTIONS

    private String description;
    private Class<?> type;

    Option(String description, Class<?> type) {
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
     * What kind of value does this option have?
     */
    public Class<?> getType() {
        return type;
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
