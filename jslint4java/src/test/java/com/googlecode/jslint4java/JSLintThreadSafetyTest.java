package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

import com.beust.jcommander.internal.Lists;

/**
 * Check that JSLint can safely be used in multiple threads.
 *
 * @see <a href="http://code.google.com/p/jslint4java/issues/detail?id=47">issue 47</a>
 */
public class JSLintThreadSafetyTest {

    private static final String EXPECTED_SEMICOLON = "Expected ';' and instead saw '(end)'.";
    private static final String JS = "var a = 'lint this'";

    @Test
    public void canRunSameInstanceInMultipleThreads() throws Exception {
        final JSLint lint = new JSLintBuilder().fromDefault();

        // Check that the first one works.
        lint.lint("foo1", JS);

        runInSeparateThread(new Callable<JSLintResult>() {
            public JSLintResult call() throws Exception {
                // Now check it still works in a different thread.
                return lint.lint("foo2", JS);
            }
        });
    }

    @Test
    public void canBuildInDifferentThread() throws Exception {
        final JSLintBuilder builder = new JSLintBuilder();
        runInSeparateThread(new Callable<JSLintResult>() {
            public JSLintResult call() {
                JSLint lint = builder.fromDefault();
                return lint.lint("blort.js", JS);
            }
        });
    }

    /**
     * We should be able to run two different threads simultaneously.
     */
    @Test
    public void shouldRunInParallel() throws InterruptedException, ExecutionException {
        JSLint lint = new JSLintBuilder().fromDefault();
        int nThreads = 3; // how many to run in parallel.
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(nThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        List<Future<JSLintResult>> results = Lists.newArrayList(nThreads);
        for (int i = 0; i < nThreads; i++) {
            String name = "thread-" + i;
            Callable<JSLintResult> callable = makeCallableLinter(lint, name, startGate, endGate);
            results.add(executorService.submit(callable));
        }

        // Initiate all at once.
        startGate.countDown();
        // Wait for everyone to finish.
        endGate.await();

        // Check the results.
        for (Future<JSLintResult> future : results) {
            JSLintResult result = future.get();
            assertThat(result.getIssues(), hasSize(1));
            assertThat(result.getIssues().get(0).getReason(), is(EXPECTED_SEMICOLON));
        }
    }

    // A small helper to make a linter which can run in an executor.
    private Callable<JSLintResult> makeCallableLinter(final JSLint lint, final String name,
            final CountDownLatch startGate, final CountDownLatch endGate) {
        return new Callable<JSLintResult>() {
            public JSLintResult call() throws Exception {
                startGate.await();
                JSLintResult result = lint.lint(name, JS);
                endGate.countDown();
                return result;
            }
        };
    }

    /**
     * Run <i>r</i> in a separate thread, and verify that it raises no exceptions.
     */
    private static void runInSeparateThread(Callable<JSLintResult> r) throws InterruptedException,
            ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(r).get();
    }
}
