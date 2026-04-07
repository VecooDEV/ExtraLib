package com.vecoo.extralib;

import com.mojang.logging.LogUtils;
import com.vecoo.extralib.config.ServerConfig;
import com.vecoo.extralib.loader.YamlLoader;
import com.vecoo.extralib.scheduler.TaskTimer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Locale;

public class ExtraLib implements ModInitializer {
    public static final String MOD_ID = "extralib";
    private static final Logger LOGGER = LogUtils.getLogger();

    private static ExtraLib instance;

    private ServerConfig serverConfig;

    private MinecraftServer server;

    @Override
    public void onInitialize() {
        instance = this;

        loadConfig();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
                    this.server = server;

                    if (this.serverConfig.isNotification()) {
                        notificationMessage();
                    }
                }
        );
        ServerTickEvents.END_SERVER_TICK.register(server -> TaskTimer.onServerTickEnd());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> TaskTimer.cancelAll());
    }

    public void loadConfig() {
        try {
            this.serverConfig = YamlLoader.load(ServerConfig.class, "config/extralib/config.yml", false);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void notificationMessage() {
        String language = Locale.getDefault().getLanguage();
        String separator = "==========================================================================";

        LOGGER.info(separator);
        if (language.equalsIgnoreCase("ru")) {
            LOGGER.info("Спасибо за использование библиотеки ExtraLib, а так же его зависимых модов!");
            LOGGER.info("");
            LOGGER.info("Официально поддерживаемые моды библиотеки:");
            LOGGER.info("ExtraQuests, LegendControl, MoveLearner, PixelmonQuests, ExtraRTP, ExtraWarp, ChunkLimiter.");
            LOGGER.info("");
            LOGGER.info("За помощью (настройка, баги, ошибки и прочее) обращаться в дискорд:");
            LOGGER.info("https://discord.gg/VSGEVagRPq");
            LOGGER.info("");
            LOGGER.info("По стандарту каждая команда запрещена для использования, так что для полноценной");
            LOGGER.info("работы команд модов необходим мод или плагин LuckPerms. Если же такового не имеется");
            LOGGER.info("загляните в /config/{mod}/permissions.yml и настройте разрешение здесь.");
            LOGGER.info("");
            LOGGER.info("Автор: Vecoo (Discord: @Vecoo)");
        } else {
            LOGGER.info("Thanks for using ExtraLib and its dependent mods!");
            LOGGER.info("");
            LOGGER.info("Officially supported mods:");
            LOGGER.info("ExtraQuests, LegendControl, MoveLearner, PixelmonQuests, ExtraRTP, ExtraWarp, ChunkLimiter.");
            LOGGER.info("");
            LOGGER.info("For help (setup, bugs, errors, etc.) join our Discord:");
            LOGGER.info("https://discord.gg/VSGEVagRPq");
            LOGGER.info("");
            LOGGER.info("By default, every command is restricted. For full functionality, LuckPerms is required.");
            LOGGER.info("If you don't have it, check /config/{mod}/permissions.yml to configure permissions.");
            LOGGER.info("");
            LOGGER.info("Author: Vecoo (Discord: @Vecoo)");
        }
        LOGGER.info(separator);
    }

    public static ExtraLib getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public MinecraftServer getServer() {
        return instance.server;
    }
}