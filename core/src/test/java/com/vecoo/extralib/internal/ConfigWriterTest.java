package com.vecoo.extralib.internal;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class ConfigWriterTest {
    @Test(timeout = 20000)
    public void blockedWriterTimesOutAndDoesNotRunACancelledSave() throws Exception {
        ConfigWriter writer = new ConfigWriter("ExtraLib-Test-Blocked-Writer");
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        AtomicBoolean saved = new AtomicBoolean();

        writer.getExecutor().execute(() -> {
            started.countDown();

            try {
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        assertTrue(started.await(3, TimeUnit.SECONDS));
        long begin = System.nanoTime();

        try {
            writer.execute("save", Path.of("blocked-test"), () -> {
                saved.set(true);
                return null;
            });
            fail("A blocked queue must time out");
        } catch (IOException expected) {
            assertTrue(expected.getCause() instanceof TimeoutException);
            assertTrue(TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - begin) >= 14);
        } finally {
            release.countDown();
        }

        try {
            writer.flush().get(3, TimeUnit.SECONDS);
            assertFalse("The cancelled queued save must not run later", saved.get());
            assertEquals(Integer.valueOf(42), writer.execute("save", Path.of("recovered-test"), () -> 42));
        } finally {
            ((ExecutorService) writer.getExecutor()).shutdownNow();
        }
    }

    @Test(timeout = 5000)
    public void interruptionIsPreservedAndCancelsTheQueuedSave() throws Exception {
        ConfigWriter writer = new ConfigWriter("ExtraLib-Test-Interrupted-Writer");
        CountDownLatch release = new CountDownLatch(1);
        AtomicBoolean saved = new AtomicBoolean();
        AtomicBoolean interrupted = new AtomicBoolean();
        AtomicReference<Throwable> failure = new AtomicReference<>();

        writer.getExecutor().execute(() -> {
            try {
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread caller = new Thread(() -> {
            Thread.currentThread().interrupt();

            try {
                writer.execute("save", Path.of("interrupted-test"), () -> {
                    saved.set(true);
                    return null;
                });
                failure.set(new AssertionError("Expected interruption"));
            } catch (IOException expected) {
                interrupted.set(Thread.currentThread().isInterrupted());
            }
        });

        try {
            caller.start();
            caller.join(3000);
            assertFalse(caller.isAlive());
            assertNull(failure.get());
            assertTrue(interrupted.get());
        } finally {
            release.countDown();
        }

        try {
            writer.flush().get(3, TimeUnit.SECONDS);
            assertFalse(saved.get());
        } finally {
            ((ExecutorService) writer.getExecutor()).shutdownNow();
        }
    }
}
