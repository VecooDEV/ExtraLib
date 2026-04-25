package com.vecoo.extralib.ui.api.elements;

import com.vecoo.extralib.ui.api.ClickTypes;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface GuiElementBuilderInterface<T extends GuiElementBuilderInterface<T>> {
    T setCallback(@NotNull GuiElementInterface.ClickCallback callback);

    default T setCallback(@NotNull GuiElementInterface.ItemClickCallback callback) {
        return this.setCallback((GuiElementInterface.ClickCallback) callback);
    }

    default T setCallback(@NotNull Runnable callback) {
        return this.setCallback((index, type, action, gui) -> callback.run());
    }

    default T setCallback(@NotNull Consumer<ClickTypes> callback) {
        return this.setCallback((index, type, action, gui) -> callback.accept(type));
    }

    GuiElementInterface build();
}
