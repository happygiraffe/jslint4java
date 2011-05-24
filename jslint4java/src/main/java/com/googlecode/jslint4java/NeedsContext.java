package com.googlecode.jslint4java;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mozilla.javascript.ContextFactory;

/**
 * An indication that this method requires access to the current JavaScript context. This indicates
 * that the method should contain within it one of:
 *
 * <ul>
 * <li>a call to {@link ContextFactory#call(org.mozilla.javascript.ContextAction)}.
 * <li>A try/finally block calling {@link ContextFactory#enterContext()} and {@link Context#exit()}
 * </ul>
 *
 * <p>
 * TODO: enforce this annotation programmatically.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
@Documented
public @interface NeedsContext {
}
