package com.vecoo.extralib.functionaltest;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Checks {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<String, Boolean> results = new LinkedHashMap<>();

    public void check(String name, boolean condition) {
        if (this.results.putIfAbsent(name, condition) != null) {
            throw new IllegalArgumentException("Duplicate test: " + name);
        }

        LOGGER.info("FUNCTIONAL_TEST {}: {}", name, condition ? "PASS" : "FAIL");
    }

    public boolean passed() {
        return !this.results.isEmpty() && this.results.values().stream().allMatch(Boolean::booleanValue);
    }

    public int size() {
        return this.results.size();
    }

    @Override
    public String toString() {
        return this.results.toString();
    }
}
