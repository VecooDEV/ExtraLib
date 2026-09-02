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
    private static final ScheduledExecutorService SCHEDULER =
            Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable, "ExtraLib-ResetScheduler");
                thread.setDaemon(true);

                return thread;
            });

    private ResetScheduler() {
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
        if (SCHEDULER.isShutdown()) {
            return;
        }

        long delay = TimeUtil.getSecondsUntilNext(resetPeriod, zoneId);

        try {
            SCHEDULER.schedule(() -> execute(resetPeriod, zoneId, sync, task), delay, TimeUnit.SECONDS);
        } catch (RejectedExecutionException ignored) {
        }
    }

    private static void execute(@NotNull ResetPeriod resetPeriod, @NotNull ZoneId zoneId, boolean sync, @NotNull Runnable task) {
        if (sync) {
            executeSync(resetPeriod, zoneId, task);
            return;
        }

        try {
            task.run();
        } catch (Throwable throwable) {
            ExtraLib.getLogger().error("An error occurred while executing reset task.", throwable);
        } finally {
            schedule(resetPeriod, zoneId, false, task);
        }
    }

    private static void executeSync(@NotNull ResetPeriod resetPeriod, @NotNull ZoneId zoneId, @NotNull Runnable task) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (server == null) {
            if (!SCHEDULER.isShutdown()) {
                schedule(resetPeriod, zoneId, true, task);
            }

            return;
        }

        server.execute(() -> {
            try {
                task.run();
            } catch (Exception e) {
                ExtraLib.getLogger().error("An error occurred while executing reset task.", e);
            } finally {
                schedule(resetPeriod, zoneId, true, task);
            }
        });
    }

    public static void shutdown() {
        SCHEDULER.shutdownNow();
    }
}