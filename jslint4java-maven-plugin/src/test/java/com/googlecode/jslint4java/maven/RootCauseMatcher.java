package com.googlecode.jslint4java.maven;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.google.common.base.Throwables;

/**
 * An hamcrest matcher implementation, which allows matching against the root cause of a
 * {@link Throwable}.
 *
 * @param <T>
 */
class RootCauseMatcher<T> extends TypeSafeMatcher<Throwable> {
    private final Matcher<T> matcher;

    private RootCauseMatcher(Matcher<T> matcher) {
        this.matcher = matcher;
    }

    @Override
    protected boolean matchesSafely(Throwable item) {
        Throwable rootCause = Throwables.getRootCause(item);
        return matcher.matches(rootCause);
    }

    public void describeTo(Description desc) {
        desc.appendText("an exception with root cause ")
            .appendDescriptionOf(matcher);
    }

    /**
     * Return a matcher that verifies the root cause of an exception is an instance of e.
     */
    public static <T> Matcher<T> rootCause(Class<? extends Throwable> e) {
        Matcher<T> m = CoreMatchers.instanceOf(e);
        return rootCause(m);
    }

    /**
     * Return a matcher that matches against the root cause of an exception.
     */
    @SuppressWarnings("unchecked")
    public static <T> Matcher<T> rootCause(Matcher<T> matcher) {
        return (Matcher<T>) new RootCauseMatcher<T>(matcher);
    }
}