package com.vecoo.extralib.internal;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class ConfigWriter {
    private static final int TIMEOUT_SECONDS = 15;

    private final ExecutorService executor;
    private volatile Thread writerThread;
    private IOException writeFailure;

    public ConfigWriter(String threadName) {
        this.executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, threadName);

            thread.setDaemon(true);
            this.writerThread = thread;
            return thread;
        });
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public <T> T execute(String operation, Path path, Operation<T> action) throws IOException {
        if (Thread.currentThread() == this.writerThread) {
            return run(operation, path, action, true);
        }

        CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
            try {
                return run(operation, path, action, false);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, this.executor);

        try {
            return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(false);
            throw new IOException(String.format("Timed out waiting to %s configuration file: %s.", operation, path), e);
        } catch (InterruptedException e) {
            future.cancel(false);
            Thread.currentThread().interrupt();
            throw new IOException(String.format("Interrupted while waiting to %s configuration file: %s.", operation, path), e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();

            if (cause instanceof IOException error) {
                throw error;
            }

            throw new IOException(String.format("Failed to %s configuration file: %s.", operation, path), cause);
        }
    }

    private <T> T run(String operation, Path path, Operation<T> action, boolean asynchronous) throws IOException {
        try {
            return action.run();
        } catch (IOException e) {
            if (operation.equals("save")) {
                this.writeFailure = e;
            }

            if (asynchronous) {
                ExtraLibLogger.getLogger().error("Failed to {} configuration file: {}.", operation, path, e);
            }

            throw e;
        }
    }

    public CompletableFuture<Void> flush() {
        return CompletableFuture.runAsync(() -> {
            IOException failure = this.writeFailure;
            this.writeFailure = null;

            if (failure != null) {
                throw new CompletionException("A configuration write failed before this flush.", failure);
            }
        }, this.executor);
    }

    @FunctionalInterface
    public interface Operation<T> {
        T run() throws IOException;
    }
}
