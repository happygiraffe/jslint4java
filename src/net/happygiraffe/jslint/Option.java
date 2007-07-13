package net.happygiraffe.jslint;

/**
 * @author dom
 * @version $Id$
 */
public enum Option {
    /**
     * If use of some browser features should be restricted.
     */
    ADSAFE("if use of some browser features should be restricted"),
    /**
     * If bitwise operators should not be allowed.
     */
    BITWISE("if bitwise operators should not be allowed"),
    /**
     * If the standard browser globals should be predefined.
     */
    BROWSER("if the standard browser globals should be predefined"),
    /**
     * If upper case HTML should be allowed.
     */
    CAP("if upper case HTML should be allowed"),
    /**
     * If debugger statements should be allowed.
     */
    DEBUG("if debugger statements should be allowed"),
    /**
     * If === should be required.
     */
    EQEQEQ("if === should be required"),
    /**
     * If eval should be allowed.
     */
    EVIL("if eval should be allowed"),
    /**
     * If HTML fragments should be allowed.
     */
    FRAGMENT("if HTML fragments should be allowed"),
    /**
     * If line breaks should not be checked.
     */
    LAXBREAK("if line breaks should not be checked"),
    /**
     * If names should be checked.
     */
    NOMEN("if names should be checked"),
    /**
     * If the scan should stop on first error.
     */
    PASSFAIL("if the scan should stop on first error"),
    /**
     * If increment/decrement should not be allowed.
     */
    PLUSPLUS("if increment/decrement should not be allowed"),
    /**
     * If the Rhino environment globals should be predefined.
     */
    RHINO("if the Rhino environment globals should be predefined"),
    /**
     * If undefined variables are errors.
     */
    UNDEF("if undefined variables are errors"),
    /**
     * If strict whitespace rules apply.
     */
    WHITE("if strict whitespace rules apply"),
    /**
     * If the Yahoo Widgets globals should be predefined.
     */
    WIDGET("if the Yahoo Widgets globals should be predefined");

    private String description;

    Option(String description) {
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
        return name().toLowerCase();
    }

    /**
     * Show this option and its description.
     */
    @Override
    public String toString() {
        return getLowerName() + "[" + getDescription() + "]";
    }
}
