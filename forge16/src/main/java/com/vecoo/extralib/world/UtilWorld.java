package com.vecoo.extralib.world;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.FolderName;

public class UtilWorld {
    public static ServerWorld getWorldByName(String worldName) {
        for (ServerWorld world : ExtraLib.getInstance().getServer().getAllLevels()) {
            if (world.dimension().location().getPath().equals(worldName)) {
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
            String directory = server.getWorldPath(new FolderName("")).toString().replace("\\", "/");
            return file.replace("%directory%", "saves/" + directory.substring(directory.lastIndexOf("/") + 1));
        }
    }
}