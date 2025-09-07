package com.vecoo.extralib.task;

import com.vecoo.extralib.ExtraLib;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class TaskTimer {
    private final Consumer<TaskTimer> consumer;
    private final long interval;
    private long currentIteration;
    private final long iterations;
    private long countdown;
    private volatile boolean expired;

    private static final Set<TaskTimer> TASKS = ConcurrentHashMap.newKeySet();

    private TaskTimer(Consumer<TaskTimer> consumer, long delay, long interval, long iterations) {
        this.consumer = consumer;
        this.countdown = delay;
        this.interval = interval;
        this.iterations = iterations;
    }

    public boolean isExpired() {
        return this.expired;
    }

    public void cancel() {
        this.expired = true;
        TASKS.remove(this);
    }

    public static void cancelAll() {
        TASKS.forEach(TaskTimer::cancel);
    }

    private void tick() {
        if (this.expired) {
            return;
        }

        if (this.countdown-- > 0) {
            return;
        }

        try {
            this.consumer.accept(this);
        } catch (Exception e) {
            ExtraLib.getLogger().error("[ExtraLib] Task execution failed", e);
            cancel();
            return;
        }

        this.currentIteration++;

        if (this.iterations == -1 || this.currentIteration < this.iterations) {
            this.countdown = this.interval;
        } else {
            this.expired = true;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Consumer<TaskTimer> consumer;
        private long delay;
        private long interval;
        private long iterations = 1;

        public Builder execute(Runnable runnable) {
            this.consumer = task -> runnable.run();
            return this;
        }

        public Builder consume(Consumer<TaskTimer> consumer) {
            this.consumer = consumer;
            return this;
        }

        public Builder delay(long delay) {
            if (delay < 0) {
                throw new IllegalArgumentException("[ExtraLib] Delay must not be below 0");
            }

            this.delay = delay;
            return this;
        }

        public Builder interval(long interval) {
            if (interval < 0) {
                throw new IllegalArgumentException("[ExtraLib] Interval must not be below 0");
            }

            this.interval = interval;
            return this;
        }

        public Builder iterations(long iterations) {
            if (iterations < -1) {
                throw new IllegalArgumentException("[ExtraLib] Iterations must not be below -1");
            }

            this.iterations = iterations;
            return this;
        }

        public Builder infinite() {
            return iterations(-1);
        }

        public Builder withoutDelay() {
            return delay(0);
        }

        public TaskTimer build() {
            if (this.consumer == null) {
                throw new IllegalStateException("[ExtraLib] Consumer must be set");
            }

            if (this.interval < 0) {
                throw new IllegalStateException("[ExtraLib] Interval must be set");
            }

            TaskTimer task = new TaskTimer(this.consumer, this.delay, this.interval, this.iterations);
            TASKS.add(task);
            return task;
        }
    }

    public static class EventHandler {
        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                Iterator<TaskTimer> iterator = TASKS.iterator();

                while (iterator.hasNext()) {
                    TaskTimer task = iterator.next();
                    task.tick();

                    if (task.expired) {
                        iterator.remove();
                    }
                }
            }
        }
    }
}