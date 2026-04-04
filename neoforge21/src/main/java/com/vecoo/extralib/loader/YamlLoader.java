package com.vecoo.extralib.loader;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("unchecked")
public final class YamlLoader {
    /**
     * Dedicated single-thread executor for write operations.
     * Ensures that save tasks are processed sequentially to avoid file access conflicts.
     */
    private static final ExecutorService WRITER_EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "ExtraLib-YAML-Writer");

        thread.setDaemon(true);
        return thread;
    });

    private YamlLoader() {
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
    public static <T> T load(@NotNull Class<T> clazz, @NotNull String path, boolean backup) throws IOException {
        return load(clazz, Path.of(path), backup);
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
     * <li>If parsing fails and {@code backup} is true, renames the broken file to .old and creates a fresh one.</li>
     * </ul>
     *
     * @param <T>    the configuration class type
     * @param clazz  the class to deserialize the YAML into
     * @param path   the {@link Path} to the file
     * @param backup whether to create a .old backup if the file is invalid
     * @return a populated configuration instance
     * @throws IOException if the file is inaccessible or invalid when backup is disabled
     */
    public static <T> T load(@NotNull Class<T> clazz, @NotNull Path path, boolean backup) throws IOException {
        try {
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                    .path(path)
                    .nodeStyle(NodeStyle.BLOCK)
                    .build();

            CommentedConfigurationNode root = loader.load();
            ObjectMapper<T> mapper = ObjectMapper.factory().get(clazz);
            T config = mapper.load(root);

            mapper.save(config, root);
            loader.save(root);

            return config;
        } catch (Exception e) {
            if (backup && Files.exists(path)) {
                Files.move(path, path.resolveSibling(path.getFileName().toString() + ".old"), StandardCopyOption.REPLACE_EXISTING);

                return load(clazz, path, false);
            }

            throw new IOException(String.format("Failed to load file: %s.", path), e);
        }
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
     * Synchronously saves an object to a YAML file.
     *
     * @param <T>            the object type
     * @param configInstance the object instance to save
     * @param path           the {@link Path} to the file
     * @throws IOException if a disk I/O error occurs
     */
    public static <T> void save(@NotNull T configInstance, @NotNull Path path) throws IOException {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                    .path(path)
                    .nodeStyle(NodeStyle.BLOCK)
                    .build();

            CommentedConfigurationNode root = loader.createNode();
            ObjectMapper<T> mapper = (ObjectMapper<T>) ObjectMapper.factory().get(configInstance.getClass());

            mapper.save(configInstance, root);
            loader.save(root);
        } catch (Exception e) {
            throw new IOException(String.format("Failed to save file: %s.", path), e);
        }
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
}
