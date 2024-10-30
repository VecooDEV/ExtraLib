package com.vecoo.extralib.world;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;

public class UtilWorld {
    public static Level getWorldByName(String worldName) {
        for (Level world : ExtraLib.getInstance().getServer().getAllLevels()) {
            if (world.dimension().location().getPath().equals(worldName.toLowerCase())) {
                return world;
            }
        }
        return null;
    }

    public static String worldDirectory(String file) {
        MinecraftServer server = ExtraLib.getInstance().getServer();
        if (server.isDedicatedServer()) {
            return file.replace("%directory%", "world");
        } else {
            String directory = server.getWorldPath(new LevelResource("")).toString().replace("\\", "/");
            return file.replace("%directory%", "saves/" + directory.substring(directory.lastIndexOf("/") + 1));
        }
    }
}