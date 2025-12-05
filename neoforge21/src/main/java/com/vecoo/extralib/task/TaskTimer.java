package com.vecoo.extralib.task;

import com.vecoo.extralib.ExtraLib;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Represents a scheduled task that executes a {@link Consumer} at a fixed interval with an optional delay and a limited number of iterations.
 * <p>
 * Tasks are automatically managed in a static set and can be ticked on server tick events.
 * </p>
 */
public class TaskTimer {
    private final Consumer<TaskTimer> consumer;
    private final long interval;
    private long currentIteration;
    private final long iterations;
    private long countdown;
    private volatile boolean expired;

    private static final Set<TaskTimer> TASKS = ConcurrentHashMap.newKeySet();

    /**
     * Constructs a new TaskTimer instance.
     *
     * @param consumer   the consumer to execute each tick
     * @param delay      initial delay in ticks before the first execution
     * @param interval   interval in ticks between executions
     * @param iterations number of times the task should execute; -1 for infinite
     */
    private TaskTimer(@NotNull Consumer<TaskTimer> consumer, long delay, long interval, long iterations) {
        this.consumer = consumer;
        this.countdown = delay;
        this.interval = interval;
        this.iterations = iterations;
    }

    /**
     * Checks if this task has expired.
     *
     * @return true if the task has finished all iterations or was cancelled, false otherwise
     */
    public boolean isExpired() {
        return this.expired;
    }

    /**
     * Cancels this task, preventing further execution.
     */
    public void cancel() {
        this.expired = true;
        TASKS.remove(this);
    }

    /**
     * Cancels all currently registered tasks.
     */
    public static void cancelAll() {
        TASKS.forEach(TaskTimer::cancel);
    }

    /**
     * Ticks the task, decrementing its countdown and executing the consumer if ready.
     */
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
            ExtraLib.logger().error("Task execution failed.", e);
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

    /**
     * Returns a new {@link Builder} instance for creating tasks.
     *
     * @return a new TaskTimer.Builder
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for creating {@link TaskTimer} instances.
     */
    public static class Builder {
        private Consumer<TaskTimer> consumer;
        private long delay;
        private long interval;
        private long iterations = 1;

        /**
         * Sets a simple {@link Runnable} to execute on each tick.
         *
         * @param runnable the runnable to execute
         * @return this builder instance
         */
        @NotNull
        public Builder execute(@NotNull Runnable runnable) {
            this.consumer = task -> runnable.run();
            return this;
        }

        /**
         * Sets a {@link Consumer} that receives the {@link TaskTimer} instance on each tick.
         *
         * @param consumer the consumer to execute
         * @return this builder instance
         */
        @NotNull
        public Builder consume(@NotNull Consumer<TaskTimer> consumer) {
            this.consumer = consumer;
            return this;
        }

        /**
         * Sets the initial delay in ticks before the first execution.
         *
         * @param delay delay in ticks, must be >= 0
         * @return this builder instance
         */
        @NotNull
        public Builder delay(long delay) {
            if (delay < 0) {
                throw new IllegalArgumentException("Delay must not be below 0.");
            }

            this.delay = delay;
            return this;
        }

        /**
         * Sets the interval in ticks between executions.
         *
         * @param interval interval in ticks, must be >= 0
         * @return this builder instance
         */
        @NotNull
        public Builder interval(long interval) {
            if (interval < 0) {
                throw new IllegalArgumentException("Interval must not be below 0.");
            }

            this.interval = interval;
            return this;
        }

        /**
         * Sets the number of iterations this task should run.
         *
         * @param iterations number of executions; -1 for infinite
         * @return this builder instance
         */
        @NotNull
        public Builder iterations(long iterations) {
            if (iterations < -1) {
                throw new IllegalArgumentException("Iterations must not be below -1.");
            }

            this.iterations = iterations;
            return this;
        }

        /**
         * Sets the task to run infinitely.
         *
         * @return this builder instance
         */
        @NotNull
        public Builder infinite() {
            return iterations(-1);
        }

        /**
         * Removes any initial delay; task executes immediately.
         *
         * @return this builder instance
         */
        @NotNull
        public Builder withoutDelay() {
            return delay(0);
        }

        /**
         * Builds and registers the {@link TaskTimer}.
         *
         * @return the newly created TaskTimer
         */
        @NotNull
        public TaskTimer build() {
            if (this.consumer == null) {
                throw new IllegalStateException("Consumer must be set.");
            }

            if (this.interval < 0) {
                throw new IllegalStateException("Interval must be set.");
            }

            TaskTimer task = new TaskTimer(this.consumer, this.delay, this.interval, this.iterations);
            TASKS.add(task);
            return task;
        }
    }

    /**
     * Event handler that ticks all registered {@link TaskTimer} instances on each server tick.
     */
    public static class EventHandler {
        /**
         * Handles server tick events and updates all tasks.
         *
         * @param event the server tick post event
         */
        @SubscribeEvent
        public void onServerTickPost(ServerTickEvent.Post event) {
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