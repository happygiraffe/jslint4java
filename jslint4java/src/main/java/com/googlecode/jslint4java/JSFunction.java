package com.googlecode.jslint4java;

import java.util.ArrayList;
import java.util.List;

/**
 * A JavaScript function definition. This is taken from JSLint's {@code .data()}
 * function.
 *
 * @author hdm
 *
 */
public class JSFunction {

    private final List<String> closure = new ArrayList<String>();
    private final List<String> exception = new ArrayList<String>();
    private final List<String> global = new ArrayList<String>();
    private final List<String> label = new ArrayList<String>();
    private int last;
    private int line;
    private String name;
    private final List<String> outer = new ArrayList<String>();
    private final List<String> params = new ArrayList<String>();
    private final List<String> unused = new ArrayList<String>();
    private final List<String> vars = new ArrayList<String>();

    // Do nothing, but make package-private.
    JSFunction() {
    }

    /**
     * The variables and parameters that are declared in the function that are
     * used by its inner functions.
     */
    public List<String> getClosure() {
        return closure;
    }

    /** The variables that are declared by try statements. */
    public List<String> getException() {
        return exception;
    }

    /**
     * Global variables that are used by this function. Keep these to a minimum.
     */
    public List<String> getGlobal() {
        return global;
    }

    /** Statement labels that are used by this function. */
    public List<String> getLabel() {
        return label;
    }

    public int getLast() {
        return last;
    }

    /** The line the function is defined on. */
    public int getLine() {
        return line;
    }

    /** The name of the function. */
    public String getName() {
        return name;
    }

    /** Variables used by this function that are declared in another function. */
    public List<String> getOuter() {
        return outer;
    }

    /** The parameters of the function. */
    public List<String> getParams() {
        return params;
    }

    /**
     * The variables that are declared in the function that are not used. This
     * may be an indication of an error.
     */
    public List<String> getUnused() {
        return unused;
    }

    /**
     * The variables that are declared in the function that are used only by the
     * function.
     */
    public List<String> getVars() {
        return vars;
    }

    void setClosure(List<String> closure) {
        this.closure.clear();
        this.closure.addAll(closure);
    }

    void setException(List<String> exception) {
        this.exception.clear();
        this.exception.addAll(exception);
    }

    void setGlobal(List<String> global) {
        this.global.clear();
        this.global.addAll(global);
    }

    void setLabel(List<String> label) {
        this.label.clear();
        this.label.addAll(label);
    }

    void setLast(int last) {
        this.last = last;
    }

    void setLine(int line) {
        this.line = line;
    }

    void setName(String name) {
        this.name = name;
    }

    void setOuter(List<String> outer) {
        this.outer.clear();
        this.outer.addAll(outer);
    }

    void setParams(List<String> params) {
        this.params.clear();
        this.params.addAll(params);
    }

    void setUnused(List<String> unused) {
        this.unused.clear();
        this.unused.addAll(unused);
    }

    void setVars(List<String> vars) {
        this.vars.clear();
        this.vars.addAll(vars);
    }

    @Override
    public String toString() {
        return String.format("function %s()", getName());
    }
}
