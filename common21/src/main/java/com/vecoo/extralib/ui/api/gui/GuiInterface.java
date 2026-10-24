package com.vecoo.extralib.ui.api.gui;

import com.vecoo.extralib.ui.api.ScreenProperty;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused"})
public interface GuiInterface {
    void setTitle(@NotNull Component title);

    Component getTitle();

    MenuType<?> getType();

    ServerPlayer getPlayer();

    int getSyncId();

    boolean isOpen();

    boolean open();

    void openForce();

    void reOpen();

    boolean getAutoUpdate();

    void setAutoUpdate(boolean value);

    @ApiStatus.Internal
    void close(boolean alreadyClosed);

    default void close() {
        this.close(false);
    }

    default void beforeOpen() {
    }

    default void afterOpen() {
    }

    default void onOpen() {
    }

    default void onClose() {
    }

    default void onTick() {
    }

    default boolean canPlayerClose() {
        return true;
    }

    default void handleException(@NotNull Throwable throwable) {
        throwable.printStackTrace();
    }

    default void sendProperty(@NotNull ScreenProperty property, int value) {
        if (!property.validFor(getType())) {
            throw new IllegalArgumentException(String.format("The property '%s' is not valid for the handler '%s'", property.name(), BuiltInRegistries.MENU.getId(getType())));
        }

        if (isOpen()) {
            getPlayer().connection.send(new ClientboundContainerSetDataPacket(getSyncId(), property.id(), value));
        }
    }

    default void sendRawProperty(int id, int value) {
        if (isOpen()) {
            getPlayer().connection.send(new ClientboundContainerSetDataPacket(getSyncId(), id, value));
        }
    }

    default boolean resetMousePosition() {
        return false;
    }
}
