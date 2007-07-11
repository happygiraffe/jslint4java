package net.happygiraffe.jslint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class JSLint {

    private static final String JSLINT_FILE = "fulljslint.js";

    private Context ctx;

    private ScriptableObject scope;

    public JSLint() throws IOException {
        ctx = Context.enter();
        scope = ctx.initStandardObjects();
        Reader reader = new BufferedReader(new InputStreamReader(getClass()
                .getResourceAsStream(JSLINT_FILE)));
        ctx.evaluateReader(scope, reader, JSLINT_FILE, 1, null);
    }

    public List<Issue> lint(String javaScript) {
        List<Issue> issues = new ArrayList<Issue>();
        scope.put("input", scope, javaScript);
        String check = "JSLINT(input,{})";
        Boolean ok = (Boolean) ctx.evaluateString(scope, check, "lint()", 1,
                null);
        if (!ok.booleanValue()) {
            readErrors(issues);
        }
        return issues;
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

}
