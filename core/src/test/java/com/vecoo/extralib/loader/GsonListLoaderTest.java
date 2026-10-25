package com.vecoo.extralib.loader;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class GsonListLoaderTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @After
    public void drainWrites() throws Exception {
        GsonLoader.flush().handle((result, error) -> null).get(3, TimeUnit.SECONDS);
    }

    @Test(timeout = 5000)
    public void customSerializersStillRoundTripLists() throws Exception {
        Path path = this.temporaryFolder.getRoot().toPath().resolve("list.json");
        TypeSerializerCollection serializers = TypeSerializerCollection.defaults();

        GsonLoader.save(List.of(new ConfigLoaderTest.Payload(23)), ConfigLoaderTest.Payload.class, path, serializers);

        List<ConfigLoaderTest.Payload> list = GsonLoader.load(ConfigLoaderTest.Payload.class, path, serializers, false);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(23, list.get(0).value);
    }

    @Test(timeout = 5000)
    public void readOnlyListIsNotTreatedAsCorrupt() throws Exception {
        Path path = this.temporaryFolder.getRoot().toPath().resolve("read-only-list.json");
        TypeSerializerCollection serializers = TypeSerializerCollection.defaults();
        GsonLoader.save(List.of(new ConfigLoaderTest.Payload(23)), ConfigLoaderTest.Payload.class, path, serializers);
        String contents = Files.readString(path);
        assertTrue(path.toFile().setReadOnly());

        try {
            try {
                List<ConfigLoaderTest.Payload> list = GsonLoader.load(ConfigLoaderTest.Payload.class, path, serializers, true);
                assertNotNull(list);
                assertEquals(23, list.get(0).value);
            } catch (IOException expected) {
                assertEquals(contents, Files.readString(path));
                assertFalse(Files.exists(path.resolveSibling(path.getFileName() + ".old")));
            }

            assertEquals(contents, Files.readString(path));
            assertFalse(Files.exists(path.resolveSibling(path.getFileName() + ".old")));
        } finally {
            path.toFile().setWritable(true);
        }
    }
}
