package net.happygiraffe.jslint;

/**
 * @author dom
 * @version $Id$
 */
public enum Option {
    ADSAFE   ("if use of some browser features should be restricted"),
    BITWISE  ("if bitwise operators should not be allowed"),
    BROWSER  ("if the standard browser globals should be predefined"),
    CAP      ("if upper case HTML should be allowed"),
    DEBUG    ("if debugger statements should be allowed"),
    EQEQEQ   ("if === should be required"),
    EVIL     ("if eval should be allowed"),
    FRAGMENT ("if HTML fragments should be allowed"),
    LAXBREAK ("if line breaks should not be checked"),
    NOMEN    ("if names should be checked"),
    PASSFAIL ("if the scan should stop on first error"),
    PLUSPLUS ("if increment/decrement should not be allowed"),
    RHINO    ("if the Rhino environment globals should be predefined"),
    UNDEF    ("if undefined variables are errors"),
    WHITE    ("if strict whitespace rules apply"),
    WIDGET   ("if the Yahoo Widgets globals should be predefined");

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
     * Provide a JavaScript form of this option.
     */
    @Override
    public String toString() {
        return getLowerName() + "[" + getDescription() + "]";
    }
}
