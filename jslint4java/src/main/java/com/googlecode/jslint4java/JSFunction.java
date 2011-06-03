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

    /**
     * Allow construction of an immutable {@link JSFunction} from outside of this package.
     */
    public static class Builder {
        private final List<String> closures = new ArrayList<String>();
        private final List<String> exceptions = new ArrayList<String>();
        private final List<String> globals = new ArrayList<String>();
        private final List<String> labels = new ArrayList<String>();
        private int last;
        private final int line;
        private final String name;
        private final List<String> outers = new ArrayList<String>();
        private final List<String> params = new ArrayList<String>();
        private final List<String> unuseds = new ArrayList<String>();
        private final List<String> undefs = new ArrayList<String>();
        private final List<String> vars = new ArrayList<String>();

        public Builder(String name, int line) {
            this.name = name;
            this.line = line;
        }

        public Builder addClosure(String closure) {
            closures.add(closure);
            return this;
        }

        public Builder addException(String exception) {
            exceptions.add(exception);
            return this;
        }

        public Builder addGlobal(String global) {
            globals.add(global);
            return this;
        }

        public Builder addLabel(String label) {
            labels.add(label);
            return this;
        }

        public Builder addOuter(String outer) {
            outers.add(outer);
            return this;
        }

        public Builder addParam(String param) {
            params.add(param);
            return this;
        }

        public Builder addUnused(String unused) {
            unuseds.add(unused);
            return this;
        }

        public Builder addUndef(String unused) {
            undefs.add(unused);
            return this;
        }

        public Builder addVar(String var) {
            vars.add(var);
            return this;
        }

        public JSFunction build() {
            return new JSFunction(this);
        }

        public Builder last(int last) {
            this.last = last;
            return this;
        }
    }

    private final List<String> closure = new ArrayList<String>();
    private final List<String> exception = new ArrayList<String>();
    private final List<String> global = new ArrayList<String>();
    private final List<String> label = new ArrayList<String>();
    private final int last;
    private final int line;
    private final String name;
    private final List<String> outer = new ArrayList<String>();
    private final List<String> params = new ArrayList<String>();
    private final List<String> unused = new ArrayList<String>();
    private final List<String> undef = new ArrayList<String>();
    private final List<String> vars = new ArrayList<String>();

    private JSFunction(Builder builder) {
        name = builder.name;
        line = builder.line;
        last = builder.last;
        closure.addAll(builder.closures);
        exception.addAll(builder.exceptions);
        global.addAll(builder.globals);
        label.addAll(builder.labels);
        outer.addAll(builder.outers);
        params.addAll(builder.params);
        unused.addAll(builder.unuseds);
        undef.addAll(builder.undefs);
        vars.addAll(builder.vars);
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
     *
     */
    public List<String> getUndef() {
        return undef;
    }

    /**
     * The variables that are declared in the function that are used only by the
     * function.
     */
    public List<String> getVars() {
        return vars;
    }

    @Override
    public String toString() {
        return String.format("function %s()", getName());
    }
}
