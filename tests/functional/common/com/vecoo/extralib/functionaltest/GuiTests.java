package com.vecoo.extralib.functionaltest;

import com.vecoo.extralib.ui.api.ClickTypes;
import com.vecoo.extralib.ui.api.GuiHelpers;
import com.vecoo.extralib.ui.api.ScreenProperty;
import com.vecoo.extralib.ui.api.elements.GuiElement;
import com.vecoo.extralib.ui.api.elements.GuiElementInterface;
import com.vecoo.extralib.ui.api.gui.SimpleGui;
import com.vecoo.extralib.ui.api.gui.SimpleGuiBuilder;
import com.vecoo.extralib.ui.api.gui.SlotGuiInterface;
import com.vecoo.extralib.ui.virtual.FakeScreenHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class GuiTests {
    private GuiTests() {
    }

    public static void run(ServerPlayer player, Checks checks) {
        SimpleGuiBuilder builder = new SimpleGuiBuilder(MenuType.GENERIC_9x1, false);
        AtomicInteger added = new AtomicInteger();
        AtomicInteger removed = new AtomicInteger();
        GuiElement element = new GuiElement(new ItemStack(Items.DIAMOND), GuiElementInterface.EMPTY_CALLBACK) {
            @Override
            public void onAdded(SlotGuiInterface gui) {
                added.incrementAndGet();
            }

            @Override
            public void onRemoved(SlotGuiInterface gui) {
                removed.incrementAndGet();
            }
        };
        Slot redirect = new Slot(new SimpleContainer(1), 0, 0, 0);
        builder.setSlotRedirect(0, redirect);
        checks.check("gui/builder redirect flag", builder.isRedirectingSlots());
        builder.setSlot(0, element);
        SimpleGui gui = builder.build(player);
        checks.check("gui/element replaces builder redirect", gui.getSlot(0) == element && gui.getSlotRedirect(0) == null);
        checks.check("gui/replaced redirect flags cleared", !builder.isRedirectingSlots() && !gui.isRedirectingSlots());
        checks.check("gui/element added callback", added.get() == 1);
        gui.setSlotRedirect(0, redirect);
        checks.check("gui/redirect removes element callback", removed.get() == 1 && gui.getSlot(0) == null);
        gui.setSlotRedirect(1, redirect);
        gui.clearSlot(0);
        checks.check("gui/clearing one redirect preserves another", gui.isRedirectingSlots());
        gui.setSlot(1, element);
        checks.check("gui/replacing last redirect clears flag", !gui.isRedirectingSlots());
        gui.clearSlot(1);
        checks.check("gui/clear removes element callback", removed.get() == 2);
        checks.check("gui/out of range reads", gui.getSlot(-1) == null && gui.getSlot(gui.getSize()) == null);

        for (int i = 0; i < builder.getSize(); i++) {
            builder.setSlot(i, new ItemStack(Items.STONE));
        }

        checks.check("gui/full builder has no empty slot", builder.getFirstEmptySlot() == -1);
        builder.clearSlot(4);
        checks.check("gui/first empty slot after clear", builder.getFirstEmptySlot() == 4);
        checks.check("gui/chest dimensions", gui.getWidth() == 9 && gui.getHeight() == 1 && gui.getSize() == 9);
        checks.check("gui/grid position", GuiHelpers.posToIndex(2, 1, 3, 9) == 11);

        boolean rejected = false;

        try {
            gui.sendProperty(ScreenProperty.FIRE_LEVEL, 5);
        } catch (IllegalArgumentException e) {
            rejected = true;
        }

        checks.check("gui/rejects property of incompatible menu", rejected);
        AtomicInteger handled = new AtomicInteger();
        IllegalStateException failure = new IllegalStateException("Functional GUI tick failure");
        SimpleGui failing = new SimpleGui(MenuType.GENERIC_9x1, player, false) {
            @Override
            public void onTick() {
                throw failure;
            }

            @Override
            public void handleException(Throwable error) {
                if (error == failure) {
                    handled.incrementAndGet();
                }
            }
        };
        new FakeScreenHandler(failing).broadcastChanges();
        checks.check("gui/tick failure reaches custom handler", handled.get() == 1);

        SimpleContainer first = new SimpleContainer(1);
        SimpleContainer second = new SimpleContainer(1);
        first.setItem(0, new ItemStack(Items.STONE, 60));
        List<Slot> slots = List.of(new Slot(first, 0, 0, 0), new Slot(second, 0, 0, 0));
        ItemStack stack = new ItemStack(Items.STONE, 10);
        gui.insertItem(stack, slots, false);
        checks.check("gui/insert merges and fills remainder", first.getItem(0).getCount() == 64 && second.getItem(0).getCount() == 6 && stack.isEmpty());
        first.clearContent();
        second.clearContent();
        gui.insertItem(new ItemStack(Items.STONE, 3), slots, true);
        checks.check("gui/reverse insert uses final slot", first.getItem(0).isEmpty() && second.getItem(0).getCount() == 3);
        checks.check("gui/reverse insert preserves caller slot order", slots.get(0).container == first && slots.get(1).container == second);
        checks.check("click/offhand swap", PlatformItems.clickType("SWAP", 40, 0) == ClickTypes.OFFHAND_SWAP);
        checks.check("click/outside drop", PlatformItems.clickType("THROW", 1, -999) == ClickTypes.MOUSE_RIGHT_OUTSIDE);
        checks.check("click/drag end", PlatformItems.clickType("QUICK_CRAFT", 6, 0) == ClickTypes.MOUSE_RIGHT_DRAG_END);
    }
}
