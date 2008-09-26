package net.happygiraffe.jslint;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
    ADSAFE("If adsafe should be enforced", BooleanOptionInstance.class),
    /** If bitwise operators should not be allowed */
    BITWISE("If bitwise operators should not be allowed", BooleanOptionInstance.class),
    /** If the standard browser globals should be predefined */
    BROWSER("If the standard browser globals should be predefined", BooleanOptionInstance.class),
    /** If upper case html should be allowed */
    CAP("If upper case html should be allowed", BooleanOptionInstance.class),
    /** If css workarounds should be tolerated */
    CSS("If css workarounds should be tolerated", BooleanOptionInstance.class),
    /** If debugger statements should be allowed */
    DEBUG("If debugger statements should be allowed", BooleanOptionInstance.class),
    /** If === should be required */
    EQEQEQ("If === should be required", BooleanOptionInstance.class),
    /** If eval should be allowed */
    EVIL("If eval should be allowed", BooleanOptionInstance.class),
    /** If for in statements must filter */
    FORIN("If for in statements must filter", BooleanOptionInstance.class),
    /** If html fragments should be allowed */
    FRAGMENT("If html fragments should be allowed", BooleanOptionInstance.class),
    /** If line breaks should not be checked */
    LAXBREAK("If line breaks should not be checked", BooleanOptionInstance.class),
    /** If names should be checked */
    NOMEN("If names should be checked", BooleanOptionInstance.class),
    /** If html event handlers should be allowed */
    ON("If html event handlers should be allowed", BooleanOptionInstance.class),
    /** If only one var statement per function should be allowed */
    ONEVAR("If only one var statement per function should be allowed", BooleanOptionInstance.class),
    /** If the scan should stop on first error */
    PASSFAIL("If the scan should stop on first error", BooleanOptionInstance.class),
    /** If increment/decrement should not be allowed */
    PLUSPLUS("If increment/decrement should not be allowed", BooleanOptionInstance.class),
    /** If the . should not be allowed in regexp literals */
    REGEXP("If the . should not be allowed in regexp literals", BooleanOptionInstance.class),
    /** If the rhino environment globals should be predefined */
    RHINO("If the rhino environment globals should be predefined", BooleanOptionInstance.class),
    /** If use of some browser features should be restricted */
    SAFE("If use of some browser features should be restricted", BooleanOptionInstance.class),
    /** If the system object should be predefined */
    SIDEBAR("If the system object should be predefined", BooleanOptionInstance.class),
    /** Require the "use strict"; pragma */
    STRICT("Require the \"use strict\"; pragma", BooleanOptionInstance.class),
    /** If all forms of subscript notation are tolerated */
    SUB("If all forms of subscript notation are tolerated", BooleanOptionInstance.class),
    /** If variables should be declared before used */
    UNDEF("If variables should be declared before used", BooleanOptionInstance.class),
    /** If strict whitespace rules apply */
    WHITE("If strict whitespace rules apply", BooleanOptionInstance.class),
    /** If the yahoo widgets globals should be predefined */
    WIDGET("If the yahoo widgets globals should be predefined", BooleanOptionInstance.class);
    // END-OPTIONS

    private String description;
    private Class<? extends OptionInstance> type;

    Option(String description, Class<? extends OptionInstance> type) {
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
    public Class<? extends OptionInstance> getType() {
        return type;
    }

    public OptionInstance getInstance() {
        try {
            Constructor<? extends OptionInstance> ctor = type
                    .getConstructor(Option.class);
            return ctor.newInstance(this);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
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
