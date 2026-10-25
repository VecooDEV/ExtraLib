package com.vecoo.extralib.loader;

import com.vecoo.extralib.internal.ConfigFile;
import com.vecoo.extralib.internal.ConfigWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import org.spongepowered.configurate.loader.ParsingException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@SuppressWarnings("unchecked")
public final class YamlLoader {
    /**
     * Dedicated single-thread executor for write operations.
     * Ensures that save tasks are processed sequentially to avoid file access conflicts.
     */
    @NotNull
    private static final ConfigWriter WRITER = new ConfigWriter("ExtraLib-YAML-Writer");

    @NotNull
    public static final Executor WRITER_EXECUTOR = WRITER.getExecutor();

    private YamlLoader() {
    }

    /**
     * Loads a YAML configuration file into a strongly-typed object.
     * <p>
     * Operation flow:
     * <ul>
     * <li>Reads the file from disk.</li>
     * <li>Maps data to the Java object.</li>
     * <li>Inserts missing fields with default values defined in the class.</li>
     * <li>Saves the updated configuration back to disk to sync defaults.</li>
     * <li>If parsing fails and {@code backup} is true, renames the broken file to .old and returns null.</li>
     * </ul>
     *
     * @param <T>    the configuration class type
     * @param clazz  the class to deserialize the YAML into
     * @param path   the {@link Path} to the file
     * @param backup whether to create a .old backup if the file is invalid
     * @return a populated configuration instance
     * @throws IOException if the file is inaccessible or invalid when backup is disabled
     */
    @Nullable
    public static <T> T load(@NotNull Class<T> clazz, @NotNull Path path, boolean backup) throws IOException {
        return WRITER.execute("load", path, () -> {
            try {
                String contents = ConfigFile.read(path);
                YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                        .path(path)
                        .source(() -> new BufferedReader(new StringReader(contents)))
                        .nodeStyle(NodeStyle.BLOCK)
                        .indent(2)
                        .build();

                ObjectMapper<T> mapper = ObjectMapper.factory().get(clazz);
                CommentedConfigurationNode root;
                T config;

                try {
                    root = loader.load();
                    config = mapper.load(root);
                } catch (ParsingException | SerializationException e) {
                    if (ConfigFile.backup(path, backup, e)) {
                        return null;
                    }

                    throw e;
                }

                mapper.save(config, root);
                loader.save(root);

                return config;
            } catch (Exception e) {
                throw new IOException(String.format("Failed to load file: %s.", path), e);
            }
        });
    }

    /**
     * Loads a YAML configuration file into a strongly-typed object.
     *
     * @param <T>    the configuration class type
     * @param clazz  the class to deserialize the YAML into
     * @param path   the string path to the file
     * @param backup whether to create a .old backup if the file is invalid
     * @return a populated configuration instance
     * @throws IOException if a critical I/O error occurs
     */
    @Nullable
    public static <T> T load(@NotNull Class<T> clazz, @NotNull String path, boolean backup) throws IOException {
        return load(clazz, Path.of(path), backup);
    }

    /**
     * Synchronously saves an object through the shared write queue to a YAML file.
     *
     * @param <T>            the object type
     * @param configInstance the object instance to save
     * @param path           the {@link Path} to the file
     * @throws IOException if a disk I/O error occurs
     */
    public static <T> void save(@NotNull T configInstance, @NotNull Path path) throws IOException {
        WRITER.execute("save", path, () -> {
            try {
                if (path.getParent() != null) {
                    Files.createDirectories(path.getParent());
                }

                YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                        .path(path)
                        .nodeStyle(NodeStyle.BLOCK)
                        .indent(2)
                        .build();

                CommentedConfigurationNode root = loader.createNode();
                ObjectMapper<T> mapper = (ObjectMapper<T>) ObjectMapper.factory().get(configInstance.getClass());

                mapper.save(configInstance, root);
                loader.save(root);
            } catch (Exception e) {
                throw new IOException(String.format("Failed to save file: %s.", path), e);
            }

            return null;
        });
    }

    /**
     * Synchronously saves an object to a YAML file.
     *
     * @param <T>            the object type
     * @param configInstance the object instance to save
     * @param path           the string path to the file
     * @throws IOException if directories cannot be created or the file cannot be written
     */
    public static <T> void save(@NotNull T configInstance, @NotNull String path) throws IOException {
        save(configInstance, Path.of(path));
    }

    /**
     * Asynchronously saves an object to a YAML file.
     *
     * @param <T>            the object type
     * @param configInstance the object instance to save
     * @param path           the {@link Path} to the file
     * @return a {@link CompletableFuture} representing the save task
     */
    public static <T> CompletableFuture<Void> saveAsync(@NotNull T configInstance, @NotNull Path path) {
        return CompletableFuture.runAsync(() -> {
            try {
                save(configInstance, path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, WRITER_EXECUTOR);
    }

    /**
     * Asynchronously saves an object to a YAML file.
     * Uses an internal single-thread executor to maintain write order.
     *
     * @param <T>            the object type
     * @param configInstance the object instance to save
     * @param path           the string path to the file
     * @return a {@link CompletableFuture} representing the save task
     */
    public static <T> CompletableFuture<Void> saveAsync(@NotNull T configInstance, @NotNull String path) {
        return saveAsync(configInstance, Path.of(path));
    }

    /**
     * Returns a future that completes after all write operations submitted before this call.
     * Completes exceptionally if a write failed since the previous flush.
     *
     * @return a barrier future for pending YAML writes
     */
    @NotNull
    public static CompletableFuture<Void> flush() {
        return WRITER.flush();
    }

}
