package com.vecoo.extralib.client.screen;

@FunctionalInterface
public interface ScaleAction {
    void scale(int scaledMouseX, int scaledMouseY);
}