package com.vecoo.extralib.ui.virtual.sign;

import com.vecoo.extralib.ui.mixin.SignBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class VirtualSignBlockEntity extends SignBlockEntity {
    public VirtualSignBlockEntity(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        super(blockPos, blockState);
        this.setLevel(level);
    }

    public boolean setText(@NotNull SignText text, boolean front) {
        return front ? this.setFrontText(text) : this.setBackText(text);
    }

    private boolean setBackText(@NotNull SignText backText) {
        if (backText != this.getBackText()) {
            ((SignBlockEntityAccessor) this).setBackText(backText);
            return true;
        } else {
            return false;
        }
    }

    private boolean setFrontText(@NotNull SignText frontText) {
        if (frontText != this.getFrontText()) {
            ((SignBlockEntityAccessor) this).setFrontText(frontText);
            return true;
        } else {
            return false;
        }
    }
}
