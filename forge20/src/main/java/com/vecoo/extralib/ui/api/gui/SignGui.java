package com.vecoo.extralib.ui.api.gui;

import com.vecoo.extralib.ui.api.GuiHelpers;
import com.vecoo.extralib.ui.virtual.FakeScreenHandler;
import com.vecoo.extralib.ui.virtual.sign.VirtualSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SignGui implements GuiInterface {
    protected final VirtualSignBlockEntity signEntity;
    protected BlockState type = Blocks.OAK_SIGN.defaultBlockState();
    protected boolean autoUpdate = true;

    protected List<Integer> sendLineUpdate = new ArrayList<>(4);
    protected final ServerPlayer player;
    protected boolean open = false;
    protected boolean reOpen = false;
    protected FakeScreenHandler screenHandler;
    private final Component[] texts = new Component[4];

    public SignGui(@NotNull ServerPlayer player) {
        this.player = player;
        this.signEntity = new VirtualSignBlockEntity(player.level(), new BlockPos(player.blockPosition().getX(), Math.min(player.level().getMaxBuildHeight(), player.blockPosition().getY() + 5), player.blockPosition().getZ()), Blocks.OAK_SIGN.defaultBlockState());
    }

    public void setLine(int line, @NotNull Component text) {
        this.signEntity.updateText(signText -> signText.setMessage(line, text), true);
        this.sendLineUpdate.add(line);
        this.texts[line] = text;

        if (this.open & this.autoUpdate) {
            this.updateSign();
        }
    }

    @NotNull
    public Component getLine(int line) {
        return this.texts[line];
    }

    public void setColor(@NotNull DyeColor color) {
        this.signEntity.updateText(signText -> signText.setColor(color), true);

        if (this.open && this.autoUpdate) {
            this.updateSign();
        }
    }

    public void setSignType(@NotNull Block type) {
        if (!(type instanceof SignBlock)) {
            throw new IllegalArgumentException("The type must be a sign.");
        }

        this.type = type.defaultBlockState();

        if (this.open && this.autoUpdate) {
            this.updateSign();
        }
    }

    public void updateSign() {
        if (this.player.containerMenu == this.screenHandler) {
            this.reOpen = true;
            this.player.connection.send(new ClientboundContainerClosePacket(this.screenHandler.containerId));
        } else {
            this.open();
        }
    }

    @Override
    public ServerPlayer getPlayer() {
        return this.player;
    }

    @Override
    public boolean isOpen() {
        return this.open;
    }

    @Override
    public boolean open() {
        this.reOpen = true;

        if (this.player.containerMenu != this.player.inventoryMenu && this.player.containerMenu != this.screenHandler) {
            this.player.closeContainer();
        }
        if (screenHandler == null) {
            this.screenHandler = new FakeScreenHandler(this);
        }
        this.player.containerMenu = this.screenHandler;

        this.player.connection.send(new ClientboundBlockUpdatePacket(this.signEntity.getBlockPos(), this.type));
        this.player.connection.send(this.signEntity.getUpdatePacket());
        this.player.connection.send(new ClientboundOpenSignEditorPacket(this.signEntity.getBlockPos(), true));

        this.reOpen = false;
        this.open = true;

        return true;
    }

    @Override
    public void openForce() {
        open();
    }

    @Override
    public void reOpen() {
        GuiInterface gui = GuiHelpers.getCurrentGui(player);

        if (gui != null) {
            gui.close();
        }

        open();
    }

    @Override
    public void close(boolean alreadyClosed) {
        if (this.open && !this.reOpen) {
            this.open = false;
            this.reOpen = false;

            this.player.connection.send(new ClientboundBlockUpdatePacket(player.level(), signEntity.getBlockPos()));

            if (alreadyClosed && this.player.containerMenu == this.screenHandler) {
                this.player.doCloseContainer();
            } else {
                this.player.closeContainer();
            }

            this.onClose();
        } else {
            this.reOpen = false;
            this.open();
        }
    }

    @Override
    public boolean getAutoUpdate() {
        return this.autoUpdate;
    }

    @Override
    public void setAutoUpdate(boolean value) {
        this.autoUpdate = value;
    }

    @ApiStatus.Internal
    public void setLineInternal(int line, @NotNull Component text) {
        if (this.reOpen && this.sendLineUpdate.contains(line)) {
            this.sendLineUpdate.remove((Integer) line);
        } else {
            this.signEntity.getFrontText().setMessage(line, text);
            this.texts[line] = text;
        }
    }

    @Deprecated
    @Override
    public void setTitle(@NotNull Component title) {
    }

    @Deprecated
    @Override
    public Component getTitle() {
        return null;
    }

    @Deprecated
    @Override
    public MenuType<?> getType() {
        return null;
    }

    @Deprecated
    @Override
    public int getSyncId() {
        return -1;
    }
}
