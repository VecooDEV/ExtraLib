package com.vecoo.extralib.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

public final class YamlConfigFactory {
    private YamlConfigFactory() {
    }

    /**
     * Loads a YAML configuration file into a strongly-typed configuration object.
     * <p>
     * This method performs the following steps:
     * <ul>
     *   <li>Loads the YAML file from the specified path.</li>
     *   <li>Deserializes its contents into an instance of the given class.</li>
     *   <li>Automatically inserts any missing configuration fields using default values
     *       defined in the class.</li>
     *   <li>Saves the updated configuration back to disk.</li>
     * </ul>
     * <p>
     * Existing user-defined values are preserved and are never overwritten.
     *
     * @param <T>   the configuration class type
     * @param clazz the configuration class to deserialize into
     * @param path  the path to the YAML configuration file
     * @return a populated configuration instance
     *
     * @throws RuntimeException if the configuration cannot be loaded or saved
     */
    public static <T> T load(@NotNull Class<T> clazz, @NotNull Path path) {
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
            throw new RuntimeException(String.format("Failed to load config: %s", path), e);
        }
    }
}
