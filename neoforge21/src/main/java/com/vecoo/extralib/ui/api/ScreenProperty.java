package com.vecoo.extralib.ui.api;

import net.minecraft.world.inventory.MenuType;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public enum ScreenProperty {
    FIRE_LEVEL(0, MenuType.FURNACE, MenuType.BLAST_FURNACE, MenuType.SMOKER),
    MAX_FUEL_BURN_TIME(1, MenuType.FURNACE, MenuType.BLAST_FURNACE, MenuType.SMOKER),
    CURRENT_PROGRESS(2, MenuType.FURNACE, MenuType.BLAST_FURNACE, MenuType.SMOKER),
    MAX_PROGRESS(3, MenuType.FURNACE, MenuType.BLAST_FURNACE, MenuType.SMOKER),
    TOP_LEVEL_REQ(0, MenuType.ENCHANTMENT),
    MIDDLE_LEVEL_REQ(1, MenuType.ENCHANTMENT),
    BOTTOM_LEVEL_REQ(2, MenuType.ENCHANTMENT),
    ENCHANT_SEED(3, MenuType.ENCHANTMENT),
    TOP_ENCHANTMENT_ID(4, MenuType.ENCHANTMENT),
    MIDDLE_ENCHANTMENT_ID(5, MenuType.ENCHANTMENT),
    BOTTOM_ENCHANTMENT_ID(6, MenuType.ENCHANTMENT),
    TOP_ENCHANTMENT_LEVEL(7, MenuType.ENCHANTMENT),
    MIDDLE_ENCHANTMENT_LEVEL(8, MenuType.ENCHANTMENT),
    BOTTOM_ENCHANTMENT_LEVEL(9, MenuType.ENCHANTMENT),
    POWER_LEVEL(0, MenuType.BEACON),
    FIRST_EFFECT(1, MenuType.BEACON),
    SECOND_EFFECT(2, MenuType.BEACON),
    LEVEL_COST(0, MenuType.ANVIL),
    BREW_TIME(0, MenuType.BREWING_STAND),
    POWDER_FUEL_TIME(1, MenuType.BREWING_STAND),
    SELECTED(0, MenuType.STONECUTTER, MenuType.LOOM, MenuType.LECTERN);

    private final int id;
    private final MenuType<?>[] types;

    ScreenProperty(int id, @NotNull MenuType<?>... types) {
        this.id = id;
        this.types = types;
    }

    public int id() {
        return id;
    }

    public boolean validFor(@NotNull MenuType<?> type) {
        return ArrayUtils.contains(types, type);
    }
}
