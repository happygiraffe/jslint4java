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
    // BEGIN-OPTIONS
    /** If adsafe should be enforced */
    ADSAFE("If adsafe should be enforced", OptionBinder.BOOLEAN),
    /** If bitwise operators should not be allowed */
    BITWISE("If bitwise operators should not be allowed", OptionBinder.BOOLEAN),
    /** If the standard browser globals should be predefined */
    BROWSER("If the standard browser globals should be predefined", OptionBinder.BOOLEAN),
    /** If upper case html should be allowed */
    CAP("If upper case html should be allowed", OptionBinder.BOOLEAN),
    /** If css workarounds should be tolerated */
    CSS("If css workarounds should be tolerated", OptionBinder.BOOLEAN),
    /** If debugger statements should be allowed */
    DEBUG("If debugger statements should be allowed", OptionBinder.BOOLEAN),
    /** If === should be required */
    EQEQEQ("If === should be required", OptionBinder.BOOLEAN),
    /** If eval should be allowed */
    EVIL("If eval should be allowed", OptionBinder.BOOLEAN),
    /** If for in statements must filter */
    FORIN("If for in statements must filter", OptionBinder.BOOLEAN),
    /** If html fragments should be allowed */
    FRAGMENT("If html fragments should be allowed", OptionBinder.BOOLEAN),
    /** If line breaks should not be checked */
    LAXBREAK("If line breaks should not be checked", OptionBinder.BOOLEAN),
    /** If names should be checked */
    NOMEN("If names should be checked", OptionBinder.BOOLEAN),
    /** If html event handlers should be allowed */
    ON("If html event handlers should be allowed", OptionBinder.BOOLEAN),
    /** If only one var statement per function should be allowed */
    ONEVAR("If only one var statement per function should be allowed", OptionBinder.BOOLEAN),
    /** If the scan should stop on first error */
    PASSFAIL("If the scan should stop on first error", OptionBinder.BOOLEAN),
    /** If increment/decrement should not be allowed */
    PLUSPLUS("If increment/decrement should not be allowed", OptionBinder.BOOLEAN),
    /** If the . should not be allowed in regexp literals */
    REGEXP("If the . should not be allowed in regexp literals", OptionBinder.BOOLEAN),
    /** If the rhino environment globals should be predefined */
    RHINO("If the rhino environment globals should be predefined", OptionBinder.BOOLEAN),
    /** If use of some browser features should be restricted */
    SAFE("If use of some browser features should be restricted", OptionBinder.BOOLEAN),
    /** If the system object should be predefined */
    SIDEBAR("If the system object should be predefined", OptionBinder.BOOLEAN),
    /** Require the "use strict"; pragma */
    STRICT("Require the \"use strict\"; pragma", OptionBinder.BOOLEAN),
    /** If all forms of subscript notation are tolerated */
    SUB("If all forms of subscript notation are tolerated", OptionBinder.BOOLEAN),
    /** If variables should be declared before used */
    UNDEF("If variables should be declared before used", OptionBinder.BOOLEAN),
    /** If strict whitespace rules apply */
    WHITE("If strict whitespace rules apply", OptionBinder.BOOLEAN),
    /** If the yahoo widgets globals should be predefined */
    WIDGET("If the yahoo widgets globals should be predefined", OptionBinder.BOOLEAN);
    // END-OPTIONS

    private String description;
    private OptionBinder binder;

    private Option(String description, OptionBinder binder) {
        this.description = description;
        this.binder = binder;
    }

    /**
     * Return a description of what this option affects.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Bind an option to a value.
     */
    public OptionInstance getInstance() {
        return binder.bind(this, null);
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
