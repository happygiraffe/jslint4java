package com.googlecode.jslint4java;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mozilla.javascript.ContextFactory;

/**
 * An indication that this method requires access to the current JavaScript context. This indicates
 * that the method should contain within it a call to
 * {@link ContextFactory#call(org.mozilla.javascript.ContextAction)}.
 *
 * <p>
 * TODO: enforce this annotation programmatically.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@Documented
public @interface NeedsContext {
}
