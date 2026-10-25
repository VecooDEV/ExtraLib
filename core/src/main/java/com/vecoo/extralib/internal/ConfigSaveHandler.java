package com.vecoo.extralib.internal;

import com.vecoo.extralib.loader.GsonLoader;
import com.vecoo.extralib.loader.YamlLoader;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class ConfigSaveHandler {
    private static final int TIMEOUT_SECONDS = 15;

    private ConfigSaveHandler() {
    }

    public static void flush() {
        try {
            CompletableFuture.allOf(YamlLoader.flush(), GsonLoader.flush()).get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            ExtraLibLogger.getLogger().error("Configuration writes did not finish within {} seconds. Server shutdown will continue; queued writes may not be saved.", TIMEOUT_SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ExtraLibLogger.getLogger().warn("Interrupted while waiting for configuration writes. Server shutdown will continue.", e);
        } catch (ExecutionException e) {
            ExtraLibLogger.getLogger().error("Failed to finish configuration writes during server shutdown.", e.getCause());
        }
    }
}
