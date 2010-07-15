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

    private final List<String> closures = new ArrayList<String>();
    private final List<String> exceptions = new ArrayList<String>();
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

    public List<String> getClosures() {
        return closures;
    }

    public List<String> getExceptions() {
        return exceptions;
    }

    public List<String> getGlobal() {
        return global;
    }

    public List<String> getLabel() {
        return label;
    }

    public int getLast() {
        return last;
    }

    public int getLine() {
        return line;
    }

    public String getName() {
        return name;
    }

    public List<String> getOuter() {
        return outer;
    }

    public List<String> getParams() {
        return params;
    }

    public List<String> getUnused() {
        return unused;
    }

    public List<String> getVars() {
        return vars;
    }

    void setClosures(List<String> closures) {
        this.closures.clear();
        this.closures.addAll(closures);
    }

    void setExceptions(List<String> exceptions) {
        this.exceptions.clear();
        this.exceptions.addAll(exceptions);
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
