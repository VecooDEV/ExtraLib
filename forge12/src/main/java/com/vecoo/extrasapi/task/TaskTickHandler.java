package com.vecoo.extrasapi.task;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TaskTickHandler {
    private static boolean active;
    private static List<UtilTask> tasks = new ArrayList<>();

    public static void addTask(@Nonnull UtilTask task) {
        if (!active) {
            MinecraftForge.EVENT_BUS.register(new TaskTickHandler());
            active = true;
        }
        tasks.add(task);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            for (UtilTask task : new ArrayList<>(tasks)) {
                task.tick();
                if (task.isExpired()) {
                    tasks.remove(task);
                }
            }
        }
    }
}