package com.vecoo.extralib.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vecoo.extralib.ExtraLib;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public abstract class UtilGson {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @NotNull
    public static CompletableFuture<Boolean> writeFileAsync(@NotNull String filePath, @NotNull String filename, @NotNull String data) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Path path = Paths.get(new File("").getAbsolutePath() + filePath, filename);
        File file = path.toFile();

        if (!Files.exists(path.getParent())) {
            file.getParentFile().mkdirs();
        }

        try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            ByteBuffer buffer = ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8));

            fileChannel.write(buffer, 0, buffer, new CompletionHandler<>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    attachment.clear();

                    try {
                        fileChannel.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    future.complete(true);
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    future.complete(writeFileSync(file, data));
                }
            });
        } catch (IOException | SecurityException e) {
            future.complete(future.complete(false));
        }

        return future;
    }

    public static boolean writeFileSync(@NotNull File file, @NotNull String data) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();
            return true;
        } catch (Exception e) {
            ExtraLib.getLogger().error("Write file sync error.");
            return false;
        }
    }

    @NotNull
    public static CompletableFuture<Boolean> readFileAsync(@NotNull String filePath, @NotNull String filename, @NotNull Consumer<String> callback) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Path path = Paths.get(new File("").getAbsolutePath() + filePath, filename);
        File file = path.toFile();

        if (!file.exists()) {
            future.complete(false);
            executor.shutdown();
            return future;
        }

        try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate((int) fileChannel.size());

            Future<Integer> readResult = fileChannel.read(buffer, 0);
            readResult.get();
            buffer.flip();

            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            String fileContent = new String(bytes, StandardCharsets.UTF_8);

            callback.accept(fileContent);

            fileChannel.close();
            executor.shutdown();
            future.complete(true);
        } catch (Exception e) {
            future.complete(readFileSync(file, callback));
            executor.shutdown();
        }
        return future;
    }

    public static boolean readFileSync(@NotNull File file, @NotNull Consumer<String> callback) {
        try {
            Scanner reader = new Scanner(file);
            StringBuilder data = new StringBuilder();

            while (reader.hasNextLine()) {
                data.append(reader.nextLine());
            }

            reader.close();
            callback.accept(data.toString());
            return true;
        } catch (Exception e) {
            ExtraLib.getLogger().error("Read file sync error.");
            return false;
        }
    }

    @NotNull
    public static File checkForDirectory(@NotNull String path) {
        File dir = new File(new File("").getAbsolutePath() + path);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    @NotNull
    public static Gson newGson() {
        return GSON;
    }
}