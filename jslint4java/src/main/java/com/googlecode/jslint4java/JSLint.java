package com.googlecode.jslint4java;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.UniqueTag;

import com.googlecode.jslint4java.Issue.IssueBuilder;
import com.googlecode.jslint4java.JSFunction.Builder;

/**
 * A utility class to check JavaScript source code for potential problems.
 *
 * @author dom
 * @version $Id$
 */
public class JSLint {

    // Uncomment to enable the rhino debugger.
    // static {
    // org.mozilla.javascript.tools.debugger.Main.mainEmbedded(null);
    // }

    /**
     * A helper class for interpreting the output of {@code JSLINT.data()}.
     */
    private static final class IdentifierConverter implements Util.Converter<JSIdentifier> {
        public JSIdentifier convert(Object obj) {
            Scriptable identifier = (Scriptable) obj;
            String name = Util.stringValue("name", identifier);
            int line = Util.intValue("line", identifier);
            return new JSIdentifier(name, line);
        }
    }

    /**
     * A helper class for interpreting the output of {@code JSLINT.data()}.
     */
    private final class JSFunctionConverter implements Util.Converter<JSFunction> {
        public JSFunction convert(Object obj) {
            Scriptable scope = (Scriptable) obj;
            String name = Util.stringValue("name", scope);
            int line = Util.intValue("line", scope);
            Builder b = new JSFunction.Builder(name, line);
            b.last(Util.intValue("last", scope));
            for (String param : Util.listValueOfType("param", String.class, scope)) {
                b.addParam(param);
            }
            for (String closure : Util.listValueOfType("closure", String.class, scope)) {
                b.addClosure(closure);
            }
            for (String var : Util.listValueOfType("var", String.class, scope)) {
                b.addVar(var);
            }
            for (String exception : Util.listValueOfType("exception", String.class, scope)) {
                b.addException(exception);
            }
            for (String outer : Util.listValueOfType("outer", String.class, scope)) {
                b.addOuter(outer);
            }
            for (String unused : Util.listValueOfType("unused", String.class, scope)) {
                b.addUnused(unused);
            }
            for (String global : Util.listValueOfType("global", String.class, scope)) {
                b.addGlobal(global);
            }
            for (String label : Util.listValueOfType("label", String.class, scope)) {
                b.addLabel(label);
            }
            return b.build();
        }
    }

    private final Map<Option, Object> options = new EnumMap<Option, Object>(Option.class);

    private final Scriptable scope;

    /**
     * Create a new {@link JSLint} object. You must pass in a {@link Scriptable}
     * which already has the {@code JSLINT} function defined.
     */
    public JSLint(Scriptable scope) {
        this.scope = scope;
    }

    /**
     * Add an option to change the behaviour of the lint. This will be passed in
     * with a value of "true".
     *
     * @param o
     *            Any {@link Option}.
     */
    public void addOption(Option o) {
        options.put(o, Boolean.TRUE);
    }

    /**
     * Add an option to change the behaviour of the lint. The option will be
     * parsed as appropriate using an {@link OptionParser}.
     *
     * @param o
     *            Any {@link Option}.
     * @param arg
     *            The value to associate with <i>o</i>.
     */
    public void addOption(Option o, String arg) {
        OptionParser optionParser = new OptionParser();
        options.put(o, optionParser.parse(o.getType(), arg));
    }

    private void doLint(String javaScript) {
        String src = javaScript == null ? "" : javaScript;
        Object[] args = new Object[] { src, optionsAsJavaScriptObject() };
        Function lintFunc = (Function) scope.get("JSLINT", scope);
        // JSLINT actually returns a boolean, but we ignore it as we always go
        // and look at the errors in more detail.
        lintFunc.call(Context.getCurrentContext(), scope, scope, args);
    }

    /**
     * Return the version of jslint in use.
     */
    public String getEdition() {
        Scriptable lintScope = (Scriptable) scope.get("JSLINT", scope);
        return (String) lintScope.get("edition", lintScope);
    }

    /**
     * Check for problems in a {@link Reader} which contains JavaScript source.
     *
     * @param systemId
     *            a filename
     * @param reader
     *            a {@link Reader} over JavaScript source code.
     *
     * @return a {@link JSLintResult}.
     */
    public JSLintResult lint(String systemId, Reader reader) throws IOException {
        return lint(systemId, Util.readerToString(reader));
    }

    /**
     * Check for problems in JavaScript source.
     *
     * @param systemId
     *            a filename
     * @param javaScript
     *            a String of JavaScript source code.
     *
     * @return a {@link JSLintResult}.
     */
    public JSLintResult lint(String systemId, String javaScript) {
        doLint(javaScript);
        JSLintResult result = new JSLintResult(systemId, readErrors(systemId));

        // Extract JSLINT.data() output and set it on the result.
        Scriptable lintScope = (Scriptable) scope.get("JSLINT", scope);
        Object o = lintScope.get("data", lintScope);
        // Real JSLINT will always have this, but some of my test stubs don't.
        if (o != UniqueTag.NOT_FOUND) {
            Function reportFunc = (Function) o;
            Scriptable data = (Scriptable) reportFunc.call(Context.getCurrentContext(), scope,
                    scope, new Object[] {});
            setResultGlobal(result, data);
            setResultUrls(result, data);
            setResultMember(result, data);
            setResultUnused(result, data);
            setResultImplieds(result, data);
            setResultJson(result, data);
            setResultFunctions(result, data);
        }

        return result;
    }

    /**
     * Turn the set of options into a JavaScript object, where the key is the
     * name of the option and the value is true.
     */
    private Scriptable optionsAsJavaScriptObject() {
        Scriptable opts = Context.getCurrentContext().newObject(scope);
        for (Entry<Option, Object> entry : options.entrySet()) {
            String key = entry.getKey().getLowerName();
            Object value = Context.javaToJS(entry.getValue(), opts);
            opts.put(key, opts, value);
        }
        return opts;
    }

    private List<Issue> readErrors(String systemId) {
        ArrayList<Issue> issues = new ArrayList<Issue>();
        Scriptable JSLINT = (Scriptable) scope.get("JSLINT", scope);
        Scriptable errors = (Scriptable) JSLINT.get("errors", JSLINT);
        int count = Util.intValue("length", errors);
        for (int i = 0; i < count; i++) {
            Scriptable err = (Scriptable) errors.get(i, errors);
            // JSLINT spits out a null when it cannot proceed.
            // TODO Should probably turn i-1th issue into a "fatal".
            if (err != null) {
                issues.add(IssueBuilder.fromJavaScript(systemId, err));
            }
        }
        return issues;
    }

    /**
     * Report on what variables / functions are in use by this code.
     *
     * @param javaScript
     * @return an HTML report.
     */
    public String report(String javaScript) {
        return report(javaScript, false);
    }

    /**
     * Report on what variables / functions are in use by this code.
     *
     * @param javaScript
     * @param errorsOnly
     *            If a report consisting solely of the problems is desired.
     * @return an HTML report.
     */
    public String report(String javaScript, boolean errorsOnly) {
        // Run the lint function itself as prep.
        doLint(javaScript);

        // The run the reporter.
        Object[] args = new Object[] { Boolean.valueOf(errorsOnly) };
        Scriptable lintScope = (Scriptable) scope.get("JSLINT", scope);
        Function reportFunc = (Function) lintScope.get("report", lintScope);
        // JSLINT actually returns a boolean, but we ignore it as we always go
        // and look at the errors in more detail.
        return (String) reportFunc.call(Context.getCurrentContext(), scope, scope, args);
    }

    /**
     * Clear out all options that have been set with {@link #addOption(Option)}.
     */
    public void resetOptions() {
        options.clear();
    }

    /**
     * Set the "functions" field of the {@link JSLintResult}.
     */
    private void setResultFunctions(JSLintResult result, Scriptable data) {
        result.setFunctions(Util.listValue("functions", data, new JSFunctionConverter()));
    }

    private void setResultGlobal(JSLintResult result, Scriptable data) {
        result.setGlobals(Util.listValueOfType("globals", String.class, data));
    }

    /**
     * Set the "implieds" field of the {@link JSLintResult}.
     */
    private void setResultImplieds(JSLintResult result, Scriptable data) {
        result.setImplieds(Util.listValue("implieds", data, new IdentifierConverter()));
    }

    private void setResultJson(JSLintResult result, Scriptable data) {
        result.setJson(Util.booleanValue("json", data));
    }

    /**
     * Set the "member" field of the {@link JSLintResult}.
     */
    private void setResultMember(JSLintResult result, Scriptable data) {
        Object o1 = data.get("member", data);
        if (o1 == UniqueTag.NOT_FOUND) {
            return;
        }
        Scriptable member = (Scriptable) o1;
        Object[] propertyIds = ScriptableObject.getPropertyIds(member);
        Map<String, Integer> members = new HashMap<String, Integer>(propertyIds.length);
        for (Object id : propertyIds) {
            String k = (String) id;
            members.put(k, Util.intValue(k, member));
        }
        result.setMember(members);
    }

    /**
     * Set the "unused" field of the {@link JSLintResult}.
     */
    private void setResultUnused(JSLintResult result, Scriptable data) {
        result.setUnused(Util.listValue("unused", data, new IdentifierConverter()));
    }

    /**
     * Set the "urls" field of the {@link JSLintResult}.
     */
    private void setResultUrls(JSLintResult result, Scriptable data) {
        result.setUrls(Util.listValueOfType("urls", String.class, data));
    }

}
