package com.vecoo.extralib.util.time;

import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public final class ResetPeriod {
    public static final ResetPeriod DAILY = days(1);
    public static final ResetPeriod WEEKLY = weeks(1);
    public static final ResetPeriod MONTHLY = months(1);
    public static final ResetPeriod QUARTERLY = months(3);
    public static final ResetPeriod YEARLY = years(1);

    @NotNull
    private final Type type;
    private final int amount;

    public ResetPeriod(@NotNull Type type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    @NotNull
    public Type getType() {
        return this.type;
    }

    public int getAmount() {
        return this.amount;
    }

    @NotNull
    public ZonedDateTime getCurrentReset(@NotNull ZonedDateTime now) {
        return switch (this.type) {
            case DAYS -> getCurrentDailyReset(now);
            case WEEKS -> getCurrentWeeklyReset(now);
            case MONTHS -> getCurrentMonthlyReset(now);
            case YEARS -> getCurrentYearlyReset(now);
        };
    }

    @NotNull
    public ZonedDateTime getNextReset(@NotNull ZonedDateTime now) {
        ZonedDateTime current = getCurrentReset(now);

        return switch (this.type) {
            case DAYS -> current.plusDays(this.amount);
            case WEEKS -> current.plusWeeks(this.amount);
            case MONTHS -> current.plusMonths(this.amount);
            case YEARS -> current.plusYears(this.amount);
        };
    }

    @NotNull
    private ZonedDateTime getCurrentDailyReset(@NotNull ZonedDateTime now) {
        long epochDay = now.toLocalDate().toEpochDay();
        long startEpochDay = Math.floorDiv(epochDay, this.amount) * this.amount;

        return LocalDate.ofEpochDay(startEpochDay).atStartOfDay(now.getZone());
    }

    @NotNull
    private ZonedDateTime getCurrentWeeklyReset(@NotNull ZonedDateTime now) {
        ZonedDateTime monday = now.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay(now.getZone());

        if (this.amount == 1) {
            return monday;
        }

        ZonedDateTime anchor = LocalDate.of(1970, 1, 5).atStartOfDay(now.getZone());

        long weeks = ChronoUnit.WEEKS.between(anchor, monday);
        long alignedWeeks = Math.floorDiv(weeks, this.amount) * this.amount;

        return anchor.plusWeeks(alignedWeeks);
    }

    @NotNull
    private ZonedDateTime getCurrentMonthlyReset(@NotNull ZonedDateTime now) {
        int absoluteMonth = now.getYear() * 12 + now.getMonthValue() - 1;
        int startMonth = Math.floorDiv(absoluteMonth, this.amount) * this.amount;

        int year = Math.floorDiv(startMonth, 12);
        int month = Math.floorMod(startMonth, 12) + 1;

        return LocalDate.of(year, month, 1).atStartOfDay(now.getZone());
    }

    @NotNull
    private ZonedDateTime getCurrentYearlyReset(@NotNull ZonedDateTime now) {
        int year = Math.floorDiv(now.getYear(), this.amount) * this.amount;

        return LocalDate.of(year, 1, 1).atStartOfDay(now.getZone());
    }

    @NotNull
    public static ResetPeriod days(int amount) {
        return new ResetPeriod(Type.DAYS, validate(amount));
    }

    @NotNull
    public static ResetPeriod weeks(int amount) {
        return new ResetPeriod(Type.WEEKS, validate(amount));
    }

    @NotNull
    public static ResetPeriod months(int amount) {
        return new ResetPeriod(Type.MONTHS, validate(amount));
    }

    @NotNull
    public static ResetPeriod years(int amount) {
        return new ResetPeriod(Type.YEARS, validate(amount));
    }

    private static int validate(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Reset period must be greater than 0.");
        }

        return amount;
    }

    public enum Type {
        DAYS,
        WEEKS,
        MONTHS,
        YEARS
    }
}