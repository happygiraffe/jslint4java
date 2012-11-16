package com.googlecode.jslint4java.maven;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import com.google.common.base.Throwables;

/**
 * An hamcrest matcher implementation, which allows matching the root cause of a {@link Throwable}.
 */
class RootCauseMatcher extends BaseMatcher<Class<? extends Throwable>> {
    private final Class<? extends Throwable> expectedRootCause;

    // TODO: should we take a Matcher instead?
    RootCauseMatcher(Class<? extends Throwable> clazz) {
        this.expectedRootCause = clazz;
    }

    public boolean matches(Object obj) {
        if (!(obj instanceof Throwable)) {
            // Not an exception
            return false;
        }
        Throwable e = (Throwable) obj;
        Throwable rootCause = Throwables.getRootCause(e);
        return rootCause.getClass().equals(expectedRootCause);
    }

    public void describeTo(Description desc) {
        desc.appendText("an exception with root cause " + expectedRootCause.getName());
    }

    public static Matcher<Class<? extends Throwable>> rootCause(Class<? extends Throwable> e) {
        return new RootCauseMatcher(e);
    }

}