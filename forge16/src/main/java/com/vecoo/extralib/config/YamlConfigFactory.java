package com.vecoo.extralib.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class YamlConfigFactory {
    private YamlConfigFactory() {
    }

    public static <T> T load(@Nonnull Class<T> clazz, @Nonnull String path) {
        return load(clazz, Paths.get(path));
    }

    public static <T> T load(@Nonnull Class<T> clazz, @Nonnull Path path) {
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