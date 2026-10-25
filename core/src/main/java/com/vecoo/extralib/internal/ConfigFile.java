package com.vecoo.extralib.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class ConfigFile {
    private ConfigFile() {
    }

    public static String read(Path path) throws IOException {
        try {
            return Files.readString(path);
        } catch (NoSuchFileException e) {
            return "";
        }
    }

    public static boolean backup(Path path, boolean backup, Exception cause) throws IOException {
        if (!backup || !Files.isRegularFile(path)) {
            return false;
        }

        Path backupPath = path.resolveSibling(path.getFileName().toString() + ".old");

        try {
            Files.move(path, backupPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.addSuppressed(cause);
            throw e;
        }

        ExtraLibLogger.getLogger().warn("Invalid configuration file {} was moved to {}.", path, backupPath, cause);
        return true;
    }
}
