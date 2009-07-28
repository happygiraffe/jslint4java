package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptableObject;

import com.googlecode.jslint4java.Issue;

/**
 * @author dom
 * @version $Id$
 */
public class IssueTest {

    private final ContextFactory contextFactory = new ContextFactory();
    private final Context cx = contextFactory.enterContext();
    private final ScriptableObject scope = cx.initStandardObjects();

    @After
    public void tearDown() throws Exception {
        Context.exit();
    }

    @Test
    public void testEmptyError() throws Exception {
        Issue issue = new Issue("foo.js", scope);
        assertThat(issue.getReason(), is(nullValue()));
        assertThat(issue.getLine(), is(1));
        assertThat(issue.getCharacter(), is(0));
    }

    @Test
    public void testNullError() throws Exception {
        Issue issue = new Issue("foo.js", null);
        assertThat(issue.getReason(), is(nullValue()));
        assertThat(issue.getLine(), is(1));
        assertThat(issue.getCharacter(), is(0));
    }

    @Test
    public void testToString() {
        scope.put("reason", scope, "you broke it");
        scope.put("line", scope, 0);
        scope.put("character", scope, 1);
        Issue issue = new Issue("foo.js", scope);
        assertThat(issue.toString(), is("foo.js:1:1:you broke it"));
    }
}
