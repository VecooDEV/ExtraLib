package com.vecoo.extralib;

import com.vecoo.extralib.config.ServerConfig;
import com.vecoo.extralib.loader.YamlLoader;
import com.vecoo.extralib.scheduler.TaskTimer;
import com.vecoo.extralib.util.ChatUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Locale;

@Mod(ExtraLib.MOD_ID)
public class ExtraLib {
    public static final String MOD_ID = "extralib";
    private static final Logger LOGGER = LogManager.getLogger();

    private static ExtraLib instance;

    private ServerConfig serverConfig;

    private MinecraftServer server;

    public ExtraLib() {
        instance = this;

        loadConfig();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new TaskTimer.EventHandler());
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        if (this.serverConfig.isNotification()) {
            notificationMessage();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerStopping(FMLServerStoppingEvent event) {
        TaskTimer.cancelAll();
    }

    public void loadConfig() {
        try {
            this.serverConfig = YamlLoader.load(ServerConfig.class, "config/extralib/config.yml", false);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void notificationMessage() {
        String separator = "==========================================================================";

        ChatUtil.broadcast("&8" + separator);

        if (Locale.getDefault().getLanguage().equalsIgnoreCase("ru")) {
            ChatUtil.broadcast("&#ff75ff&lСпа&#e08bff&lси&#c2a1ff&lбо &#a3b7ff&lза &#85cdff&lис&#5ce1ff&lпользование ExtraLib!");
            ChatUtil.broadcast("");
            ChatUtil.broadcast("&e&nОфициально поддерживаемые моды библиотеки:&r");
            ChatUtil.broadcast("&aExtraQuests, LegendControl, MoveLearner, PixelmonQuests, ExtraRTP, ExtraWarp, ChunkLimiter.");
            ChatUtil.broadcast("");
            ChatUtil.broadcast("&#ffcc00За помощью (настройка, баги, ошибки) обращаться в дискорд:");
            ChatUtil.broadcast("&#5865f2&nhttps://discord.gg/VSGEVagRPq");
            ChatUtil.broadcast("");
            ChatUtil.broadcast("&fПо стандарту каждая команда &cзапрещена&f. Для полноценной работы");
            ChatUtil.broadcast("&fнеобходим &#ff8000LuckPerms&f. Если его нет, настройте разрешения в:");
            ChatUtil.broadcast("&7/config/{mod}/permissions.yml");
            ChatUtil.broadcast("&7&oОтключить это уведомление: /config/extralib/config.yml");
            ChatUtil.broadcast("");
            ChatUtil.broadcast("&#ff75ffА&#ef66e6в&#df57cdт&#cf48b4о&#be399bр&#be2ed6: Vecoo (Discord: @Vecoo)");
        } else {
            ChatUtil.broadcast("&#ff75ff&lTha&#e08bff&lnks &#c2a1ff&lfor &#a3b7ff&lus&#85cdff&ling &#5ce1ff&lExtraLib!");
            ChatUtil.broadcast("");
            ChatUtil.broadcast("&e&nOfficially supported mods:&r");
            ChatUtil.broadcast("&aExtraQuests, LegendControl, MoveLearner, PixelmonQuests, ExtraRTP, ExtraWarp, ChunkLimiter.");
            ChatUtil.broadcast("");
            ChatUtil.broadcast("&#ffcc00For help (setup, bugs, errors, etc.) join our Discord:");
            ChatUtil.broadcast("&#5865f2&nhttps://discord.gg/VSGEVagRPq");
            ChatUtil.broadcast("");
            ChatUtil.broadcast("&fBy default, every command is &crestricted&f. For full functionality,");
            ChatUtil.broadcast("&#ff8000LuckPerms &fis required. Otherwise, configure permissions in:");
            ChatUtil.broadcast("&7/config/{mod}/permissions.yml");
            ChatUtil.broadcast("&7&oTo disable this notification: /config/extralib/config.yml");
            ChatUtil.broadcast("");
            ChatUtil.broadcast("&#ff75ffA&#ef66e6u&#df57cdt&#cf48b4h&#be399bo&#be2ed6r: Vecoo (Discord: @Vecoo)");
        }

        ChatUtil.broadcast("&8" + separator);
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