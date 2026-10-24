package com.vecoo.extralib.util.time;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class TimeUtil {
    public static final ZoneId DEFAULT_ZONE = ZoneId.of("Europe/Moscow");

    private TimeUtil() {
    }

    public static long getCurrentEpoch(@NotNull ResetPeriod resetPeriod, @NotNull ZoneId zoneId) {
        return getCurrentReset(resetPeriod, zoneId).toEpochSecond();
    }

    public static long getCurrentEpoch(@NotNull ResetPeriod resetPeriod) {
        return getCurrentEpoch(resetPeriod, DEFAULT_ZONE);
    }

    public static long getNextEpoch(@NotNull ResetPeriod resetPeriod, @NotNull ZoneId zoneId) {
        return getNextReset(resetPeriod, zoneId).toEpochSecond();
    }

    public static long getNextEpoch(@NotNull ResetPeriod resetPeriod) {
        return getNextEpoch(resetPeriod, DEFAULT_ZONE);
    }

    public static long getSecondsUntilNext(@NotNull ResetPeriod resetPeriod, @NotNull ZoneId zoneId) {
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime nextReset = resetPeriod.getNextReset(now);

        Duration duration = Duration.between(now, nextReset);

        if (duration.isNegative() || duration.isZero()) {
            return 0;
        }

        long seconds = duration.getSeconds();

        return duration.getNano() == 0 ? seconds : seconds + 1;
    }

    public static long getSecondsUntilNext(@NotNull ResetPeriod resetPeriod) {
        return getSecondsUntilNext(resetPeriod, DEFAULT_ZONE);
    }

    @NotNull
    public static ZonedDateTime getCurrentReset(@NotNull ResetPeriod resetPeriod, @NotNull ZoneId zoneId) {
        return resetPeriod.getCurrentReset(ZonedDateTime.now(zoneId));
    }

    @NotNull
    public static ZonedDateTime getCurrentReset(@NotNull ResetPeriod resetPeriod) {
        return getCurrentReset(resetPeriod, DEFAULT_ZONE);
    }

    @NotNull
    public static ZonedDateTime getNextReset(@NotNull ResetPeriod resetPeriod, @NotNull ZoneId zoneId) {
        return resetPeriod.getNextReset(ZonedDateTime.now(zoneId));
    }

    @NotNull
    public static ZonedDateTime getNextReset(@NotNull ResetPeriod resetPeriod) {
        return getNextReset(resetPeriod, DEFAULT_ZONE);
    }
}