package com.doo.xenchantment.util;

import com.doo.xenchantment.XEnchantment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * 补充工具
 */
public class ConfigUtil {
    public static final Logger LOGGER = LogManager.getLogger();

    private static final Path path = FileSystems.getDefault().getPath("config", XEnchantment.MOD_NAME + ".json");

    protected static final Gson JSON = new GsonBuilder().setPrettyPrinting().create();

    public static JsonObject load() {
        try {
            return read(FileChannel.open(path, StandardOpenOption.READ));
        } catch (NoSuchFileException ignored) {
            // 文件不存在（file not found）
        } catch (Exception e) {
            LOGGER.warn("Read File Error {}: ", path, e);
        }
        return new JsonObject();
    }

    private static JsonObject read(FileChannel open) throws Exception {
        ByteBuffer bb = ByteBuffer.allocate((int) open.size());
        if (open.size() < 1) {
            return new JsonObject();
        }
        open.read(bb);
        return JSON.fromJson(new String(bb.array(), StandardCharsets.UTF_8), JsonObject.class);
    }

    public static void write(JsonObject value) {
        try (FileChannel open = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            open.truncate(0);
            open.write(ByteBuffer.wrap(JSON.toJson(value).getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            LOGGER.warn("Write File Error {}: {}", path, value, e);
        }
    }
}
