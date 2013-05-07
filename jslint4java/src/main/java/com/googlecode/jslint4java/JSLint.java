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
     * A helper class for interpreting the output of {@code JSLINT.data()}.
     */
    private static final class JSFunctionConverter implements Util.Converter<JSFunction> {
        public JSFunction convert(Object obj) {
            Scriptable scope = (Scriptable) obj;
            String name = Util.stringValue("name", scope);
            int line = Util.intValue("line", scope);
            Builder b = new JSFunction.Builder(name, line);
            b.last(Util.intValue("last", scope));
            for (String param : Util.listValueOfType("parameter", String.class, scope)) {
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

    private final ContextFactory contextFactory;

    private final Function lintFunc;

    /**
     * Create a new {@link JSLint} object. You must pass in a {@link Function}, which is the JSLINT
     * function defined by jslint.js. You are expected to use {@link JSLintBuilder} rather than
     * calling this constructor.
     */
    JSLint(ContextFactory contextFactory, Function lintFunc) {
        this.contextFactory = contextFactory;
        this.lintFunc = lintFunc;
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
                Object o = lintFunc.get("data", lintFunc);
                // Real JSLINT will always have this, but some of my test stubs don't.
                if (o != UniqueTag.NOT_FOUND) {
                    Function reportFunc = (Function) o;
                    Scriptable data = (Scriptable) reportFunc.call(cx, lintFunc, null,
                            new Object[] {});
                    for (String global : Util.listValueOfType("global", String.class, data)) {
                        b.addGlobal(global);
                    }
                    b.json(Util.booleanValue("json", data));
                    for (JSFunction f : Util.listValue("functions", data, new JSFunctionConverter())) {
                        b.addFunction(f);
                    }
                }

                // Extract the list of properties. Note that we don't expose the counts, as it
                // doesn't seem that useful.
                Object properties = lintFunc.get("property", lintFunc);
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

                // Look up JSLINT.data.
                value = lintFunc.get("data", lintFunc);
                if (value == UniqueTag.NOT_FOUND) {
                    return "";
                }
                fn = (Function) value;
                // Call JSLINT.data().  This returns a JS data structure that we need below.
                Object data = fn.call(cx, lintFunc, null, new Object[] {});

                // Look up JSLINT.error_report.
                value = lintFunc.get("error_report", lintFunc);
                // Shouldn't happen ordinarily, but some of my tests don't have it.
                if (value != UniqueTag.NOT_FOUND) {
                    fn = (Function) value;
                    // Call JSLint.report().
                    sb.append(fn.call(cx, lintFunc, null, new Object[] { data }));
                }

                if (!errorsOnly) {
                    // Look up JSLINT.report.
                    value = lintFunc.get("report", lintFunc);
                    // Shouldn't happen ordinarily, but some of my tests don't have it.
                    if (value != UniqueTag.NOT_FOUND) {
                        fn = (Function) value;
                        // Call JSLint.report().
                        sb.append(fn.call(cx, lintFunc, null, new Object[] { data }));
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
                // JSLINT actually returns a boolean, but we ignore it as we always go
                // and look at the errors in more detail.
                lintFunc.call(cx, lintFunc, null, args);
                return null;
            }
        });
    }

    /**
     * Return the version of jslint in use.
     */
    public String getEdition() {
        return (String) lintFunc.get("edition", lintFunc);
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
        // This is synchronized, even though Rhino is thread safe, because we have multiple
        // accesses to the scope, which store state in between them.  This synchronized block
        // is slightly larger than I would like, but in practical terms, it doesn't make much
        // difference.  The cost of running lint is larger than the cost of pulling out the
        // results.
        synchronized (this) {
            long before = System.nanoTime();
            doLint(javaScript);
            long after = System.nanoTime();
            return buildResults(systemId, before, after);
        }
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
                Scriptable opts = cx.newObject(lintFunc);
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
        Scriptable errors = (Scriptable) lintFunc.get("errors", lintFunc);
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
