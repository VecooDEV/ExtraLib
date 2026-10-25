package com.vecoo.extralib.loader;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ConfigLoaderTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final boolean yaml;

    public ConfigLoaderTest(boolean yaml) {
        this.yaml = yaml;
    }

    @Parameterized.Parameters(name = "yaml={0}")
    public static Collection<Object[]> formats() {
        return Arrays.asList(new Object[]{false}, new Object[]{true});
    }

    @ConfigSerializable
    public static class Payload {
        public int value = 42;

        public Payload() {
        }

        public Payload(int value) {
            this.value = value;
        }
    }

    private Executor executor() {
        return this.yaml ? YamlLoader.WRITER_EXECUTOR : GsonLoader.WRITER_EXECUTOR;
    }

    private void save(Payload payload, Path path) throws IOException {
        if (this.yaml) {
            YamlLoader.save(payload, path);
        } else {
            GsonLoader.save(payload, path);
        }
    }

    private CompletableFuture<Void> saveAsync(Payload payload, Path path) {
        return this.yaml ? YamlLoader.saveAsync(payload, path) : GsonLoader.saveAsync(payload, path);
    }

    private Payload load(Path path, boolean backup) throws IOException {
        return this.yaml ? YamlLoader.load(Payload.class, path, backup) : GsonLoader.load(Payload.class, path, backup);
    }

    private CompletableFuture<Void> flush() {
        return this.yaml ? YamlLoader.flush() : GsonLoader.flush();
    }

    @After
    public void drainWrites() throws Exception {
        flush().handle((result, error) -> null).get(3, TimeUnit.SECONDS);
    }

    @Test(timeout = 5000)
    public void createsMissingConfigurationWithDefaults() throws Exception {
        Path path = this.temporaryFolder.getRoot().toPath().resolve("nested/config");

        assertEquals(42, load(path, false).value);
        assertTrue(Files.isRegularFile(path));
    }

    @Test(timeout = 5000)
    public void synchronousSaveFollowsEarlierAsyncSave() throws Exception {
        checkWriteOrder(false);
    }

    @Test(timeout = 5000)
    public void synchronousSaveFollowsEarlierPublicExecutorTask() throws Exception {
        checkWriteOrder(true);
    }

    private void checkWriteOrder(boolean publicExecutor) throws Exception {
        Path path = this.temporaryFolder.getRoot().toPath().resolve("ordered-config");
        CountDownLatch writerStarted = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();

        executor().execute(() -> {
            writerStarted.countDown();

            try {
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        assertTrue(writerStarted.await(3, TimeUnit.SECONDS));

        CompletableFuture<Void> older = publicExecutor ? CompletableFuture.runAsync(() -> {
            try {
                save(new Payload(1), path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executor()) : saveAsync(new Payload(1), path);

        Thread synchronous = new Thread(() -> {
            try {
                save(new Payload(2), path);
            } catch (Throwable e) {
                failure.set(e);
            }
        });

        synchronous.start();

        try {
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);

            while (synchronous.isAlive() && synchronous.getState() != Thread.State.TIMED_WAITING && System.nanoTime() < deadline) {
                Thread.sleep(5);
            }

            assertTrue("Synchronous save must wait behind older queued writes", synchronous.isAlive());
            assertFalse(Files.exists(path));
        } finally {
            release.countDown();
            synchronous.join(3000);
        }

        assertFalse(synchronous.isAlive());
        assertNull(failure.get());
        older.get(3, TimeUnit.SECONDS);
        flush().get(3, TimeUnit.SECONDS);
        assertEquals(2, load(path, false).value);
    }

    @Test(timeout = 5000)
    public void saveFromWriterThreadDoesNotWaitForItself() throws Exception {
        Path path = this.temporaryFolder.getRoot().toPath().resolve("nested-save");

        CompletableFuture.runAsync(() -> {
            try {
                save(new Payload(7), path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executor()).get(3, TimeUnit.SECONDS);

        assertEquals(7, load(path, false).value);
    }

    @Test(timeout = 5000)
    public void asyncFailureIsLoggedAndReportedByFlush() throws Exception {
        Path path = this.temporaryFolder.newFolder("not-a-file").toPath();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream previous = System.err;

        try (PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setErr(capture);

            try {
                saveAsync(new Payload(1), path).get(3, TimeUnit.SECONDS);
                fail("The save must fail");
            } catch (ExecutionException expected) {
                assertNotNull(expected.getCause());
            }

            try {
                flush().get(3, TimeUnit.SECONDS);
                fail("Flush must report a preceding write failure");
            } catch (ExecutionException expected) {
                assertNotNull(expected.getCause());
            }
        } finally {
            System.setErr(previous);
        }

        String log = output.toString(StandardCharsets.UTF_8);
        assertTrue(log, log.contains(path.toString()));
        assertTrue(log, log.contains("Failed to save configuration file"));
        assertTrue(log, log.contains("IOException"));
        flush().get(3, TimeUnit.SECONDS);

        Path recovered = this.temporaryFolder.getRoot().toPath().resolve("recovered");
        save(new Payload(9), recovered);
        assertEquals(9, load(recovered, false).value);
    }

    @Test(timeout = 5000)
    public void validReadOnlyFileIsNotBackedUp() throws Exception {
        Path path = this.temporaryFolder.getRoot().toPath().resolve("read-only");
        Path backup = path.resolveSibling(path.getFileName() + ".old");
        save(new Payload(42), path);
        String contents = Files.readString(path);
        assertTrue(path.toFile().setReadOnly());

        try {
            try {
                Payload loaded = load(path, true);
                assertNotNull(loaded);
                assertEquals(42, loaded.value);
            } catch (IOException expected) {
                assertTrue(Files.isRegularFile(path));
                assertFalse(Files.exists(backup));
                assertEquals(contents, Files.readString(path));
            }

            assertTrue(Files.isRegularFile(path));
            assertFalse(Files.exists(backup));
            assertEquals(contents, Files.readString(path));
        } finally {
            path.toFile().setWritable(true);
        }
    }

    @Test(timeout = 5000)
    public void invalidDataIsBackedUpWithoutLosingItsContents() throws Exception {
        Path path = this.temporaryFolder.getRoot().toPath().resolve("invalid");
        String invalid = this.yaml ? "value: [broken" : "{\"value\": [broken";
        Files.writeString(path, invalid);

        assertNull(load(path, true));
        assertFalse(Files.exists(path));
        assertEquals(invalid, Files.readString(path.resolveSibling(path.getFileName() + ".old")));
    }

    @Test(timeout = 5000)
    public void readFailureDoesNotMoveADirectory() throws Exception {
        Path path = this.temporaryFolder.newFolder("directory").toPath();

        try {
            load(path, true);
            fail("A directory cannot be loaded as a configuration file");
        } catch (IOException expected) {
            assertTrue(Files.isDirectory(path));
            assertFalse(Files.exists(path.resolveSibling(path.getFileName() + ".old")));
        }
    }

    @Test(timeout = 5000)
    public void incompatibleFieldDataIsBackedUp() throws Exception {
        Path path = this.temporaryFolder.getRoot().toPath().resolve("incompatible");
        String invalid = this.yaml ? "value: {unexpected: true}" : "{\"value\": {\"unexpected\": true}}";
        Files.writeString(path, invalid);

        assertNull(load(path, true));
        assertEquals(invalid, Files.readString(path.resolveSibling(path.getFileName() + ".old")));
    }
}
