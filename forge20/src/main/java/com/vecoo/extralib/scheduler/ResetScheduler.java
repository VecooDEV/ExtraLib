package com.vecoo.extralib.scheduler;

import com.vecoo.extralib.ExtraLib;
import com.vecoo.extralib.util.time.ResetPeriod;
import com.vecoo.extralib.util.time.TimeUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import java.time.ZoneId;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ResetScheduler {
    private static volatile ScheduledExecutorService scheduler = createExecutor();

    private ResetScheduler() {
    }

    private static ScheduledExecutorService createExecutor() {
        return Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "ExtraLib-ResetScheduler");
            thread.setDaemon(true);
            return thread;
        });
    }

    public static synchronized void start() {
        if (scheduler.isShutdown()) {
            scheduler = createExecutor();
        }
    }

    public static void schedule(@NotNull ResetPeriod resetPeriod, @NotNull Runnable task) {
        schedule(resetPeriod, TimeUtil.DEFAULT_ZONE, false, task);
    }

    public static void schedule(@NotNull ResetPeriod resetPeriod, boolean sync, @NotNull Runnable task) {
        schedule(resetPeriod, TimeUtil.DEFAULT_ZONE, sync, task);
    }

    public static void schedule(@NotNull ResetPeriod resetPeriod, @NotNull ZoneId zoneId, @NotNull Runnable task) {
        schedule(resetPeriod, zoneId, false, task);
    }

    public static void schedule(@NotNull ResetPeriod resetPeriod, @NotNull ZoneId zoneId, boolean sync, @NotNull Runnable task) {
        schedule(scheduler, resetPeriod, zoneId, sync, task);
    }

    private static void schedule(ScheduledExecutorService executor, ResetPeriod period, ZoneId zone, boolean sync, Runnable task) {
        if (executor != scheduler || executor.isShutdown()) {
            return;
        }
        try {
            executor.schedule(() -> execute(executor, period, zone, sync, task), TimeUtil.getSecondsUntilNext(period, zone), TimeUnit.SECONDS);
        } catch (RejectedExecutionException ignored) {
        }
    }

    private static void execute(ScheduledExecutorService executor, ResetPeriod period, ZoneId zone, boolean sync, Runnable task) {
        if (executor != scheduler || executor.isShutdown()) {
            return;
        }
        Runnable action = () -> {
            if (executor != scheduler || executor.isShutdown()) {
                return;
            }
            try {
                task.run();
            } catch (Throwable error) {
                ExtraLib.getLogger().error("An error occurred while executing reset task.", error);
            } finally {
                schedule(executor, period, zone, sync, task);
            }
        };
        if (sync) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                schedule(executor, period, zone, true, task);
            } else {
                server.execute(action);
            }
        } else {
            action.run();
        }
    }

    public static synchronized void shutdown() {
        scheduler.shutdownNow();
    }
}
