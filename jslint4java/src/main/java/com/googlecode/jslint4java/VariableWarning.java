package com.googlecode.jslint4java;

/**
 * A warning about a variable, as reported by JSLint.
 */
public class VariableWarning {

    private final String name;
    private final int line;
    private final String function;

    public VariableWarning(String name, int line, String function) {
        this.name = name;
        this.line = line;
        this.function = function;
    }

    /** The function containing the undefined variable. */
    public String getFunction() {
        return function;
    }

    /** The line on which the undefined variable was defined. */
    public int getLine() {
        return line;
    }

    /** The name of the undefined variable. */
    public String getName() {
        return name;
    }
}
