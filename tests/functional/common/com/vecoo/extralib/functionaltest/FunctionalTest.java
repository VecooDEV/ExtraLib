package com.vecoo.extralib.functionaltest;

import com.mojang.logging.LogUtils;
import com.vecoo.extralib.scheduler.TaskTimer;
import com.vecoo.extralib.ui.api.ClickTypes;
import com.vecoo.extralib.ui.api.GuiHelpers;
import com.vecoo.extralib.ui.api.elements.GuiElementInterface;
import com.vecoo.extralib.ui.api.gui.SimpleGui;
import com.vecoo.extralib.util.PlayerUtil;
import com.vecoo.extralib.util.TextUtil;
import com.vecoo.extralib.util.WorldUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class FunctionalTest {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Checks checks = new Checks();
    private final List<ClickTypes> clicks = new ArrayList<>();
    private final AtomicInteger guiErrors = new AtomicInteger();
    private final AtomicInteger finiteRuns = new AtomicInteger();
    private final AtomicInteger zeroRuns = new AtomicInteger();
    private final AtomicInteger cancelledRuns = new AtomicInteger();
    private SimpleGui gui;
    private SimpleGui deferred;
    private int phase;
    private int ticks;
    private int phaseTick;
    private int closed;
    private boolean finished;

    public void tick(MinecraftServer server) {
        if (this.finished) {
            return;
        }

        ServerPlayer player = server.getPlayerList().getPlayerByName("ExtraLibTest");

        if (player == null) {
            return;
        }

        this.ticks++;

        try {
            if (this.ticks > 240) {
                throw new IllegalStateException("Timed out in UI phase " + this.phase + ", clicks=" + this.clicks);
            }

            switch (this.phase) {
                case 0 -> start(player);
                case 1 -> finishButtons(player);
                case 2 -> finishLocked(player);
                case 3 -> finishDeferred(player);
                case 4 -> {
                    if (!this.deferred.isOpen() && this.ticks - this.phaseTick >= 5) {
                        this.checks.check("gui/deferred window closes", this.deferred.getSyncId() == -1 && player.containerMenu == player.inventoryMenu);
                        this.checks.check("timer/zero iterations", this.zeroRuns.get() == 0);
                        this.checks.check("timer/finite iterations", this.finiteRuns.get() == 2);
                        this.checks.check("timer/cancelled task", this.cancelledRuns.get() == 0);
                        finish(server, null);
                    }
                }
            }
        } catch (Throwable e) {
            finish(server, e);
        }
    }

    private void start(ServerPlayer player) {
        ItemTests.run(player, this.checks);
        PlatformItems.additionalChecks(player, this.checks);
        GuiTests.run(player, this.checks);
        PlayerUtil.playSound(player, SoundEvents.PLAYER_LEVELUP, 0.31F, false);
        PlayerUtil.playSoundLevel(player, SoundEvents.PLAYER_LEVELUP, 0.32F, false);
        Path expected = PlatformItems.level(player).getServer().getWorldPath(LevelResource.ROOT).normalize().resolve("storage/test.json");
        this.checks.check("world/uses actual world directory", Path.of(WorldUtil.resolveWorldDirectory("%directory%/storage/test.json", PlatformItems.level(player).getServer())).normalize().equals(expected));
        this.checks.check("text/legacy color codes converted", TextUtil.formatMessage("&aHello").getString().equals("\u00a7aHello"));
        TaskTimer zero = TaskTimer.builder().iterations(0).execute(this.zeroRuns::incrementAndGet).build();
        this.checks.check("timer/zero expires immediately", zero.isExpired());
        TaskTimer.builder().iterations(2).interval(1).execute(this.finiteRuns::incrementAndGet).build();
        TaskTimer cancelled = TaskTimer.builder().infinite().interval(1).execute(this.cancelledRuns::incrementAndGet).build();
        cancelled.cancel();
        player.getInventory().setItem(0, new ItemStack(Items.STONE, 4));
        this.gui = new SimpleGui(MenuType.GENERIC_9x1, player, false) {
            @Override
            public void onClose() {
                closed++;
            }

            @Override
            public void handleException(Throwable error) {
                if ("Functional button failure".equals(error.getMessage())) {
                    guiErrors.incrementAndGet();
                } else {
                    super.handleException(error);
                }
            }
        };
        this.gui.setTitle(Component.literal("Functional/buttons"));
        this.gui.setSlot(0, new ItemStack(Items.DIAMOND), (GuiElementInterface.ItemClickCallback) (index, type, action) -> this.clicks.add(type));
        this.gui.setSlot(1, new ItemStack(Items.BARRIER), (GuiElementInterface.ItemClickCallback) (index, type, action) -> {
            throw new IllegalStateException("Functional button failure");
        });
        this.checks.check("gui/opens actual player menu", this.gui.open() && GuiHelpers.getCurrentGui(player) == this.gui && this.gui.getSyncId() == player.containerMenu.containerId);
        this.phase = 1;
    }

    private void finishButtons(ServerPlayer player) {
        if (this.gui.isOpen()) {
            return;
        }

        this.checks.check("network/left right shift swap drop callbacks", this.clicks.equals(List.of(ClickTypes.MOUSE_LEFT, ClickTypes.MOUSE_RIGHT, ClickTypes.MOUSE_LEFT_SHIFT, ClickTypes.NUM_KEY_2, ClickTypes.DROP)));
        this.checks.check("network/callback exception reaches handler", this.guiErrors.get() == 1);
        this.checks.check("network/button cannot be taken", ItemTests.held(player, Items.DIAMOND) == 0 && ItemTests.held(player, Items.STONE) == 4 && player.containerMenu.getCarried().isEmpty());
        this.checks.check("network/close restores inventory menu", player.containerMenu == player.inventoryMenu && this.gui.getSyncId() == -1 && this.closed == 1);
        this.gui = new SimpleGui(MenuType.GENERIC_9x1, player, false) {
            @Override
            public boolean canPlayerClose() {
                return false;
            }
        };
        this.gui.setTitle(Component.literal("Functional/locked"));
        this.gui.open();
        this.phase = 2;
        this.phaseTick = this.ticks;
    }

    private void finishLocked(ServerPlayer player) {
        if (this.ticks - this.phaseTick < 15) {
            return;
        }

        this.checks.check("network/locked menu rejects client close", this.gui.isOpen() && GuiHelpers.getCurrentGui(player) == this.gui);
        this.gui.close();
        this.checks.check("gui/server can close locked menu", player.containerMenu == player.inventoryMenu && this.gui.getSyncId() == -1);
        SimpleGui first = new SimpleGui(MenuType.GENERIC_9x1, player, false);
        first.setTitle(Component.literal("Functional/deferred-first"));
        first.open();
        this.deferred = new SimpleGui(MenuType.GENERIC_9x1, player, false);
        this.deferred.setTitle(Component.literal("Functional/deferred-second"));
        this.deferred.safeOpen();
        this.checks.check("gui/safeOpen waits for current menu", first.isOpen() && !this.deferred.isOpen() && SimpleGui.PENDING_OPENS.containsKey(player.getUUID()));
        this.phase = 3;
    }

    private void finishDeferred(ServerPlayer player) {
        if (!this.deferred.isOpen()) {
            return;
        }

        this.checks.check("gui/deferred open consumes pending action", !SimpleGui.PENDING_OPENS.containsKey(player.getUUID()) && GuiHelpers.getCurrentGui(player) == this.deferred);
        this.phase = 4;
        this.phaseTick = this.ticks;
    }

    private void finish(MinecraftServer server, Throwable error) {
        this.finished = true;

        if (error != null) {
            LOGGER.error("FUNCTIONAL_TEST failed in phase {}", this.phase, error);
            this.checks.check("unexpected exception", false);
        }

        try {
            Files.writeString(Path.of("functional-results.txt"), "passed=" + this.checks.passed() + "\nchecks=" + this.checks.size() + "\n" + this.checks);
        } catch (Exception e) {
            LOGGER.error("Unable to write functional test result", e);
        }

        LOGGER.info("FUNCTIONAL_TEST_RESULT passed={} checks={}", this.checks.passed(), this.checks.size());
        server.halt(false);
    }
}

