package com.googlecode.jslint4java;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import com.google.common.base.Throwables;

/**
 * Check that JSLint can safely be used in multiple threads.
 *
 * @see <a href="http://code.google.com/p/jslint4java/issues/detail?id=47">issue 47</a>
 */
public class JSLintThreadSafetyTest {

    private static final String JS = "var a = 'lint this'";

    @Test
    public void canRunSameInstanceInMultipleThreads() throws Exception {
        final JSLint lint = new JSLintBuilder().fromDefault();

        // Check that the first one works.
        lint.lint("foo1", JS);

        assertNoRaise(new Runnable() {
            public void run() {
                // Now check it still works in a different thread.
                lint.lint("foo2", JS);
            }
        });
    }

    @Test
    public void canBuildInDifferentThread() throws Exception {
        final JSLintBuilder builder = new JSLintBuilder();
        assertNoRaise(new Runnable() {
            public void run() {
                JSLint lint = builder.fromDefault();
                lint.lint("blort.js", JS);
            }
        });
    }

    /**
     * Run <i>r</i> in a separate thread, and verify that it raises no exceptions.
     */
    private static void assertNoRaise(final Runnable r) throws InterruptedException {
        final AtomicReference<Throwable> kaboom = new AtomicReference<Throwable>();
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    r.run();
                } catch (Throwable e) {
                    kaboom.set(e);
                }
            }
        });
        t.start();
        t.join();

        if (kaboom.get() != null) {
            // Wrap for stack trace.
            throw Throwables.propagate(kaboom.get());
        }
    }

}
