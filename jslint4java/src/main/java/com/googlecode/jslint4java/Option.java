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
    /** If adsafe rules should be enforced */
    ADSAFE("If adsafe rules should be enforced", Boolean.class),

    /** If bitwise operators should be allowed */
    BITWISE("If bitwise operators should be allowed", Boolean.class),

    /** If the standard browser globals should be predefined */
    BROWSER("If the standard browser globals should be predefined", Boolean.class),

    /** If upper case html should be allowed */
    CAP("If upper case html should be allowed", Boolean.class),

    /** If types can be used inconsistently */
    CONFUSION("If types can be used inconsistently", Boolean.class),

    /** If the continuation statement should be tolerated */
    CONTINUE("If the continuation statement should be tolerated", Boolean.class),

    /** If css workarounds should be tolerated */
    CSS("If css workarounds should be tolerated", Boolean.class),

    /** If debugger statements should be allowed */
    DEBUG("If debugger statements should be allowed", Boolean.class),

    /** If logging should be allowed (console, alert, etc.) */
    DEVEL("If logging should be allowed (console, alert, etc.)", Boolean.class),

    /** If == should be allowed */
    EQEQ("If == should be allowed", Boolean.class),

    /** If es5 syntax should be allowed */
    ES5("If es5 syntax should be allowed", Boolean.class),

    /** If eval should be allowed */
    EVIL("If eval should be allowed", Boolean.class),

    /** If for in statements need not filter */
    FORIN("If for in statements need not filter", Boolean.class),

    /** If html fragments should be allowed */
    FRAGMENT("If html fragments should be allowed", Boolean.class),

    /** The indentation factor */
    INDENT("The indentation factor", Integer.class),

    /** The maximum number of errors to allow */
    MAXERR("The maximum number of errors to allow", Integer.class),

    /** The maximum length of a source line */
    MAXLEN("The maximum length of a source line", Integer.class),

    /** If constructor names capitalization is ignored */
    NEWCAP("If constructor names capitalization is ignored", Boolean.class),

    /** If node.js globals should be predefined */
    NODE("If node.js globals should be predefined", Boolean.class),

    /** If names may have dangling _ */
    NOMEN("If names may have dangling _", Boolean.class),

    /** If html event handlers should be allowed */
    ON("If html event handlers should be allowed", Boolean.class),

    /** If the scan should stop on first error */
    PASSFAIL("If the scan should stop on first error", Boolean.class),

    /** If increment/decrement should be allowed */
    PLUSPLUS("If increment/decrement should be allowed", Boolean.class),

    /** The names of predefined global variables */
    PREDEF("The names of predefined global variables", StringArray.class),

    /** If all property names must be declared with /*properties*&#47; */
    PROPERTIES("If all property names must be declared with /*properties*/", Boolean.class),

    /** If the . should be allowed in regexp literals */
    REGEXP("If the . should be allowed in regexp literals", Boolean.class),

    /** If the rhino environment globals should be predefined */
    RHINO("If the rhino environment globals should be predefined", Boolean.class),

    /** If use of some browser features should be restricted */
    SAFE("If use of some browser features should be restricted", Boolean.class),

    /** If the 'use strict'; pragma is optional */
    SLOPPY("If the 'use strict'; pragma is optional", Boolean.class),

    /** If all forms of subscript notation are tolerated */
    SUB("If all forms of subscript notation are tolerated", Boolean.class),

    /** If variables can be declared out of order */
    UNDEF("If variables can be declared out of order", Boolean.class),

    /** If unused parameters should be tolerated */
    UNPARAM("If unused parameters should be tolerated", Boolean.class),

    /** If multiple var statements per function should be allowed */
    VARS("If multiple var statements per function should be allowed", Boolean.class),

    /** If sloppy whitespace is tolerated */
    WHITE("If sloppy whitespace is tolerated", Boolean.class),

    /** If the yahoo widgets globals should be predefined */
    WIDGET("If the yahoo widgets globals should be predefined", Boolean.class),

    /** If ms windows-specific globals should be predefined */
    WINDOWS("If ms windows-specific globals should be predefined", Boolean.class),

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
