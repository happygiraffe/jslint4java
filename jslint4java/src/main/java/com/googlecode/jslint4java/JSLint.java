package com.googlecode.jslint4java;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.UniqueTag;

import com.googlecode.jslint4java.Issue.IssueBuilder;
import com.googlecode.jslint4java.JSFunction.Builder;
import com.googlecode.jslint4java.JSLintResult.ResultBuilder;
import com.googlecode.jslint4java.Util.Converter;

/**
 * A utility class to check JavaScript source code for potential problems.
 *
 * @author dom
 * @see JSLintBuilder Construction of JSLint
 */
public class JSLint {

    // Uncomment to enable the rhino debugger.
    // static {
    // org.mozilla.javascript.tools.debugger.Main.mainEmbedded(null);
    // }

    /** What should {@code indent} be set to by default? */
    private static final String DEFAULT_INDENT = "4";

    /** What should {@code maxerr} be set to by default? */
    private static final String DEFAULT_MAXERR = "50";

    /**
     * A helper class for interpreting function parameter names. Note that we
     * use this to work around a change in JSLint's data structure, which
     * changed from being a single string to an object.
     */
    private static final class JSFunctionParamConverter implements Util.Converter<String> {
        public String convert(Object obj) {
            Scriptable scope = (Scriptable) obj;
            return Util.stringValue("string", scope);
        }
    }

    /**
     * A helper class for interpreting the output of {@code JSLINT.data()}.
     */
    private static final class JSFunctionConverter implements Util.Converter<JSFunction> {
        public JSFunction convert(Object obj) {
            Scriptable scope = (Scriptable) obj;
            String name = Util.stringValue("name", scope);
            int line = Util.intValue("line", scope);
            Builder b = new JSFunction.Builder(name, line);
            b.last(Util.intValue("last", scope));
            for (String param : Util.listValue("params", scope, new JSFunctionParamConverter())) {
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
            for (String undef : Util.listValueOfType("undef", String.class, scope)) {
                b.addUndef(undef);
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

    /**
     * A helper class for looking up undefined variable objects.
     */
    private static class VariableWarningConverter implements Converter<VariableWarning> {
        public VariableWarning convert(Object obj) {
            Scriptable scope = (Scriptable) obj;
            String name = Util.stringValue("name", scope);
            int line = Util.intValue("line", scope);
            String function = Util.stringValue("function", scope);
            return new VariableWarning(name, line, function);
        }
    }

    private final Map<Option, Object> options = new EnumMap<Option, Object>(Option.class);

    private final ScriptableObject scope;

    private final ContextFactory contextFactory;

    /**
     * Create a new {@link JSLint} object. You must pass in a {@link Scriptable}
     * which already has the {@code JSLINT} function defined. You are expected
     * to use {@link JSLintBuilder} rather than calling this constructor.
     */
    JSLint(ContextFactory contextFactory, ScriptableObject scope) {
        this.contextFactory = contextFactory;
        this.scope = scope;
        // We should no longer be updating this.
        this.scope.sealObject();
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

    /**
     * Set options that should always be present. This mirrors what jslint.com
     * does.
     */
    private void applyDefaultOptions() {
        if (!options.containsKey(Option.INDENT)) {
            addOption(Option.INDENT, DEFAULT_INDENT);
        }
        if (!options.containsKey(Option.MAXERR)) {
            addOption(Option.MAXERR, DEFAULT_MAXERR);
        }
    }

    /**
     * Assemble the {@link JSLintResult} object.
     */
    @NeedsContext
    private JSLintResult buildResults(final String systemId, final long startNanos, final long endNanos) {
        return (JSLintResult) contextFactory.call(new ContextAction() {
            public Object run(Context cx) {
                ResultBuilder b = new JSLintResult.ResultBuilder(systemId);
                b.duration(TimeUnit.NANOSECONDS.toMillis(endNanos - startNanos));
                for (Issue issue : readErrors(systemId)) {
                    b.addIssue(issue);
                }

                // Collect a report on what we've just linted.
                b.report(callReport(false));

                // Extract JSLINT.data() output and set it on the result.
                Scriptable lintScope = (Scriptable) scope.get("JSLINT", scope);
                Object o = lintScope.get("data", lintScope);
                // Real JSLINT will always have this, but some of my test stubs don't.
                if (o != UniqueTag.NOT_FOUND) {
                    Function reportFunc = (Function) o;
                    Scriptable data = (Scriptable) reportFunc.call(cx, scope,
                            scope, new Object[] {});
                    for (String global : Util.listValueOfType("globals", String.class, data)) {
                        b.addGlobal(global);
                    }
                    for (String url : Util.listValueOfType("urls", String.class, data)) {
                        b.addUrl(url);
                    }
                    b.json(Util.booleanValue("json", data));
                    for (JSFunction f : Util.listValue("functions", data, new JSFunctionConverter())) {
                        b.addFunction(f);
                    }
                    if (Boolean.TRUE.equals(options.get(Option.WARNINGS))) {
                        // Pull out the list of unused variables as issues. This doesn't appear
                        // to be a supported API, unfortunately, but it does work.
                        for (VariableWarning u : Util.listValue("unused", data,
                                new VariableWarningConverter())) {
                            b.addIssue(issueForUnusedVariable(u, systemId));
                        }
                        // There is also a list of undefined variables.  But… these are already
                        // reported by JSLint.  So let's not repeat them.
                    }
                }

                // Extract the list of properties. Note that we don't expose the counts, as it
                // doesn't seem that useful.
                Object properties = lintScope.get("property", lintScope);
                if (properties != UniqueTag.NOT_FOUND) {
                    for (Object id: ScriptableObject.getPropertyIds((Scriptable) properties)) {
                        b.addProperty(id.toString());
                    }
                }

                return b.build();
            }
        });
    }

    /**
     * Construct a JSLint error report. This is in two parts: a list of errors, and an optional
     * function report.
     *
     * @param errorsOnly
     *            if the function report should be omitted.
     * @return the report, an HTML string.
     */
    @NeedsContext
    private String callReport(final boolean errorsOnly) {
        return (String) contextFactory.call(new ContextAction() {
            // TODO: This would probably benefit from injecting an API to manage JSLint.
            public Object run(Context cx) {
                Function fn = null;
                Object value = null;
                StringBuilder sb = new StringBuilder();
                Scriptable lintScope = (Scriptable) scope.get("JSLINT", scope);

                // Look up JSLINT.data.
                value = lintScope.get("data", lintScope);
                if (value == UniqueTag.NOT_FOUND) {
                    return "";
                }
                fn = (Function) value;
                // Call JSLINT.data().  This returns a JS data structure that we need below.
                Object data = fn.call(cx, scope, scope, new Object[] {});

                // Look up JSLINT.error_report.
                value = lintScope.get("error_report", lintScope);
                // Shouldn't happen ordinarily, but some of my tests don't have it.
                if (value != UniqueTag.NOT_FOUND) {
                    fn = (Function) value;
                    // Call JSLint.report().
                    sb.append(fn.call(cx, scope, scope, new Object[] { data }));
                }

                if (!errorsOnly) {
                    // Look up JSLINT.report.
                    value = lintScope.get("report", lintScope);
                    // Shouldn't happen ordinarily, but some of my tests don't have it.
                    if (value != UniqueTag.NOT_FOUND) {
                        fn = (Function) value;
                        // Call JSLint.report().
                        sb.append(fn.call(cx, scope, scope, new Object[] { data }));
                    }
                }
                return sb.toString();
            }
        });
    }

    @NeedsContext
    private void doLint(final String javaScript) {
        contextFactory.call(new ContextAction() {
            public Object run(Context cx) {
                String src = javaScript == null ? "" : javaScript;
                Object[] args = new Object[] { src, optionsAsJavaScriptObject() };
                Function lintFunc = (Function) scope.get("JSLINT", scope);
                // JSLINT actually returns a boolean, but we ignore it as we always go
                // and look at the errors in more detail.
                lintFunc.call(cx, scope, scope, args);
                return null;
            }
        });
    }

    /**
     * Return the version of jslint in use.
     */
    public String getEdition() {
        Scriptable lintScope = (Scriptable) scope.get("JSLINT", scope);
        return (String) lintScope.get("edition", lintScope);
    }

    private Issue issueForUnusedVariable(VariableWarning u, String systemId) {
        String reason = String.format("Unused variable: '%s' in %s.", u.getName(), u.getFunction());
        return new IssueBuilder(systemId, u.getLine(), 0, reason).build();
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
        long before = System.nanoTime();
        doLint(javaScript);
        long after = System.nanoTime();
        return buildResults(systemId, before, after);
    }

    /**
     * Turn the set of options into a JavaScript object, where the key is the
     * name of the option and the value is true.
     */
    @NeedsContext
    private Scriptable optionsAsJavaScriptObject() {
        return (Scriptable) contextFactory.call(new ContextAction() {
            public Object run(Context cx) {
                applyDefaultOptions();
                Scriptable opts = cx.newObject(scope);
                for (Entry<Option, Object> entry : options.entrySet()) {
                    String key = entry.getKey().getLowerName();
                    // Use our "custom" version in order to get native arrays.
                    Object value = Util.javaToJS(entry.getValue(), opts);
                    opts.put(key, opts, value);
                }
                return opts;
            }
        });
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
        return callReport(errorsOnly);
    }

    /**
     * Clear out all options that have been set with {@link #addOption(Option)}.
     */
    public void resetOptions() {
        options.clear();
    }
}
