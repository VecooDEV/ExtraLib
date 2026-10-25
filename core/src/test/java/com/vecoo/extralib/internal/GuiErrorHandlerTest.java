package com.vecoo.extralib.internal;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.Assert.*;

public class GuiErrorHandlerTest {
    @Test
    public void preservesContextAndStackTraceWithoutFloodingTheLog() {
        Object gui = new Object();
        UUID player = new UUID(1, 2);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream previous = System.err;

        try (PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setErr(capture);

            for (int i = 0; i < 40; i++) {
                GuiErrorHandler.log(gui, player, 7, new IllegalStateException("GUI audit failure"));
            }
        } finally {
            System.setErr(previous);
        }

        String log = output.toString(StandardCharsets.UTF_8);
        assertTrue(log, log.contains(player.toString()));
        assertTrue(log, log.contains("sync ID 7"));
        assertTrue(log, log.contains("java.lang.IllegalStateException: GUI audit failure"));
        assertTrue(log, log.contains("GuiErrorHandlerTest.java:"));
        assertEquals(1, log.split("GUI audit failure", -1).length - 1);
    }

    @Test
    public void distinctErrorsInTheSameGuiAreStillLogged() {
        Object gui = new Object();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream previous = System.err;

        try (PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setErr(capture);
            GuiErrorHandler.log(gui, new UUID(1, 2), 7, new IllegalStateException("First GUI failure"));
            GuiErrorHandler.log(gui, new UUID(1, 2), 7, new IllegalArgumentException("Different GUI failure"));
        } finally {
            System.setErr(previous);
        }

        String log = output.toString(StandardCharsets.UTF_8);
        assertTrue(log, log.contains("First GUI failure"));
        assertTrue(log, log.contains("Different GUI failure"));
    }
}
