package net.happygiraffe.jslint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * A utility class to check JavaScript source code for potential problems.
 * 
 * @author dom
 * @version $Id$
 */
public class JSLint {

    private static final String JSLINT_FILE = "fulljslint.js";

    private Context ctx;

    private Set<Option> options = EnumSet.noneOf(Option.class);

    private ScriptableObject scope;

    /**
     * Create a new {@link JSLint} object. This reads in the jslint JavaScript
     * source as a resource.
     * 
     * @throws IOException
     *                 if something went wrong reading jslint.js.
     */
    public JSLint() throws IOException {
        ctx = Context.enter();
        scope = ctx.initStandardObjects();
        Reader reader = new BufferedReader(new InputStreamReader(getClass()
                .getResourceAsStream(JSLINT_FILE)));
        ctx.evaluateReader(scope, reader, JSLINT_FILE, 1, null);
    }

    /**
     * Add an option to change the behaviour of the lint.
     * 
     * @param o
     *                Any {@link Option}.
     */
    public void addOption(Option o) {
        options.add(o);
    }

    /**
     * Check for problems in JavaScript source.
     * 
     * @param javaScript
     *                a String of JavaScript source code.
     * @return a {@link List} of {@link Issue}s describing any problems.
     */
    public List<Issue> lint(String javaScript) {
        List<Issue> issues = new ArrayList<Issue>();
        scope.put("input", scope, javaScript == null ? "" : javaScript);
        setJavaScriptOptions("options");
        String check = "JSLINT(input, options)";
        Boolean ok = (Boolean) ctx.evaluateString(scope, check, "lint()", 1,
                null);
        if (!ok.booleanValue()) {
            readErrors(issues);
        }
        return issues;
    }

    private String optionsToObjectLiteral() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (Option o : options) {
            sb.append(o.toString());
            sb.append(",");
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }

    private void readErrors(List<Issue> issues) {
        Scriptable JSLINT = (Scriptable) scope.get("JSLINT", scope);
        Scriptable errors = (Scriptable) JSLINT.get("errors", JSLINT);
        int count = Util.intValue("length", errors);
        for (int i = 0; i < count; i++) {
            Scriptable err = (Scriptable) errors.get(i, errors);
            issues.add(new Issue(err));
        }
    }

    private void setJavaScriptOptions(String name) {
        String js = "var " + name + " = " + optionsToObjectLiteral();
        ctx.evaluateString(scope, js, "-", 1, null);
    }

}
