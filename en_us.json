package com.rogic.client.sound;

import com.rogic.LaowuMemeMod;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

/** 每条音频的启用状态，缺省为启用。 */
public final class EnabledConfig {
    private static final String FILE_NAME = "enabled.properties";

    private EnabledConfig() {}

    public static Path getFile() {
        return AudioPool.getConfigDir().resolve(FILE_NAME);
    }

    public static void load(Map<String, Boolean> enabled) {
        Path file = getFile();
        if (!Files.isRegularFile(file)) return;

        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException exception) {
            LaowuMemeMod.LOGGER.warn("[laowu meme] 无法读取音频配置 {}", file, exception);
            return;
        }

        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            enabled.put(key, !"false".equalsIgnoreCase(value) && !"0".equals(value));
        }
    }

    public static void save(Map<String, Boolean> enabled) {
        Path file = getFile();
        try {
            Files.createDirectories(file.getParent());
            Properties properties = new Properties();
            for (Map.Entry<String, Boolean> entry : enabled.entrySet()) {
                if (Boolean.FALSE.equals(entry.getValue())) {
                    properties.setProperty(entry.getKey(), "false");
                }
            }
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                properties.store(writer, "laowu_meme disabled audio entries; missing=true");
            }
        } catch (IOException exception) {
            LaowuMemeMod.LOGGER.warn("[laowu meme] 无法保存音频配置 {}", file, exception);
        }
    }
}
