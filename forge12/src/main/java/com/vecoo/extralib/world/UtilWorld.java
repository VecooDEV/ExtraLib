package com.vecoo.extralib.world;

import com.vecoo.extralib.ExtraLib;
import net.minecraft.server.MinecraftServer;

public class UtilWorld {
    public static String worldDirectory(String file) {
        MinecraftServer server = ExtraLib.getInstance().getServer();
        if (server.isDedicatedServer()) {
            return file.replace("%directory%", "world");
        } else {
            return file.replace("%directory%", "saves/" + server.getFolderName());
        }
    }
}