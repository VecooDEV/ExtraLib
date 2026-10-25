package com.vecoo.extralib.internal;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public final class GuiErrorHandler {
    private static final long LOG_INTERVAL = TimeUnit.SECONDS.toNanos(5);
    private static final Map<Object, ErrorState> ERRORS = new WeakHashMap<>();

    private GuiErrorHandler() {
    }

    public static void log(Object gui, UUID playerUUID, int syncId, Throwable error) {
        int suppressed;
        long now = System.nanoTime();
        StackTraceElement[] trace = error.getStackTrace();
        String signature = error.getClass().getName() + ":" + error.getMessage() + ":" + (trace.length == 0 ? "" : trace[0]);

        synchronized (ERRORS) {
            ErrorState state = ERRORS.get(gui);

            if (state != null && state.signature.equals(signature) && now - state.lastLogged < LOG_INTERVAL) {
                state.suppressed++;
                return;
            }

            suppressed = state == null ? 0 : state.suppressed;
            ERRORS.put(gui, new ErrorState(now, signature));
        }

        if (suppressed > 0) {
            ExtraLibLogger.getLogger().error("GUI {} failed for player {} (sync ID {}). Suppressed {} repeated GUI errors.", gui.getClass().getName(), playerUUID, syncId, suppressed, error);
            return;
        }

        ExtraLibLogger.getLogger().error("GUI {} failed for player {} (sync ID {}).", gui.getClass().getName(), playerUUID, syncId, error);
    }

    private static final class ErrorState {
        private final long lastLogged;
        private final String signature;
        private int suppressed;

        private ErrorState(long lastLogged, String signature) {
            this.lastLogged = lastLogged;
            this.signature = signature;
        }
    }
}
