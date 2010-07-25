package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptableObject;

import com.googlecode.jslint4java.Issue.IssueBuilder;

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
        Issue issue = IssueBuilder.fromJavaScript("foo.js", scope);
        assertThat(issue.getReason(), is(nullValue()));
        assertThat(issue.getLine(), is(1));
        assertThat(issue.getCharacter(), is(1));
    }

    @Test
    public void testNullError() throws Exception {
        Issue issue = IssueBuilder.fromJavaScript("foo.js", null);
        assertThat(issue.getReason(), is(nullValue()));
        assertThat(issue.getLine(), is(1));
        assertThat(issue.getCharacter(), is(1));
    }

    @Test
    public void testToString() {
        Issue issue = new IssueBuilder("foo.js", 1, 1, "you broke it").build();
        assertThat(issue.toString(), is("foo.js:1:1:you broke it"));
    }
}
