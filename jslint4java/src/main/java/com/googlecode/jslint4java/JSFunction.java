package com.googlecode.jslint4java;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private final int last;
    private final int line;
    private final String name;
    private final List<String> outer = new ArrayList<String>();
    private final List<String> params = new ArrayList<String>();
    private final List<String> unused = new ArrayList<String>();
    private final List<String> vars = new ArrayList<String>();

    public JSFunction(String name, int line, int last, Map<String, List<String>> info) {
        this.name = name;
        this.line = line;
        this.last = last;
        params.addAll(info.get("params"));
        closures.addAll(info.get("closures"));
        vars.addAll(info.get("vars"));
        exceptions.addAll(info.get("exceptions"));
        outer.addAll(info.get("outer"));
        unused.addAll(info.get("unused"));
        global.addAll(info.get("global"));
        label.addAll(info.get("label"));
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

}
