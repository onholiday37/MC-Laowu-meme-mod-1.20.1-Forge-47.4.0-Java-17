package com.rogic.client.sound;

import com.rogic.LaowuMemeMod;
import com.rogic.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

/** 客户端本地音频池：可用的内置 OGG 与 config 中导入的 OGG 一起随机。 */
public final class AudioPool {
    private static final Map<String, Supplier<SoundEvent>> BUILTINS = new LinkedHashMap<>();
    public static final Map<String, String> BUILTIN_DISPLAY = new LinkedHashMap<>();

    static {
        BUILTINS.put("laowu2", ModSounds.LAOWU2);
        BUILTINS.put("qiliang", ModSounds.QILIANG);
        BUILTINS.put("zhanhou", ModSounds.ZHANHOU);
        BUILTIN_DISPLAY.put("laowu2", "[那个那个]");
        BUILTIN_DISPLAY.put("qiliang", "[老吴凄凉]");
        BUILTIN_DISPLAY.put("zhanhou", "[战吼]");
    }

    private static final List<String> imported = new ArrayList<>();
    private static final Set<String> disabledKeys = new LinkedHashSet<>();
    private static boolean initialized;

    private AudioPool() {}

    public static synchronized void init() {
        if (initialized) return;
        initialized = true;
        refreshImported();
        Map<String, Boolean> loaded = new LinkedHashMap<>();
        EnabledConfig.load(loaded);
        loaded.forEach((key, value) -> {
            if (Boolean.FALSE.equals(value)) disabledKeys.add(key);
        });
    }

    public static synchronized void refreshImported() {
        imported.clear();
        Path directory = getSoundsDir();
        try {
            Files.createDirectories(directory);
            try (Stream<Path> files = Files.list(directory)) {
                files.filter(Files::isRegularFile)
                        .map(path -> path.getFileName().toString())
                        .filter(name -> name.toLowerCase(Locale.ROOT).endsWith(".ogg"))
                        .map(AudioPool::stripExtension)
                        .sorted(Comparator.comparing(name -> name.toLowerCase(Locale.ROOT)))
                        .forEach(imported::add);
            }
        } catch (IOException exception) {
            LaowuMemeMod.LOGGER.warn("[laowu meme] 无法扫描外部 OGG 目录 {}", directory, exception);
        }
    }

    public static synchronized List<String> importedNames() {
        return List.copyOf(imported);
    }

    public static synchronized List<String> importedKeys() {
        return imported.stream().map(name -> "imported:" + name).toList();
    }

    public static List<String> builtinKeys() {
        return List.copyOf(BUILTINS.keySet());
    }

    public static boolean isEnabled(String key) {
        return !disabledKeys.contains(key);
    }

    public static synchronized void setEnabled(String key, boolean enabled) {
        if (enabled) disabledKeys.remove(key);
        else disabledKeys.add(key);
        persist();
    }

    public static synchronized boolean toggleEnabled(String key) {
        boolean enabled = !isEnabled(key);
        setEnabled(key, enabled);
        return enabled;
    }

    public static boolean isBuiltinAvailable(String name) {
        return BUILTINS.containsKey(name) && hasBuiltinOgg(name);
    }

    public static synchronized PlayTarget random() {
        init();
        refreshImported();
        List<PlayTarget> choices = new ArrayList<>();
        for (Map.Entry<String, Supplier<SoundEvent>> entry : BUILTINS.entrySet()) {
            String key = entry.getKey();
            if (isEnabled("builtin:" + key) && hasBuiltinOgg(key)) {
                choices.add(PlayTarget.builtin(entry.getValue().get()));
            }
        }
        for (String name : imported) {
            if (isEnabled("imported:" + name)) choices.add(PlayTarget.imported(name));
        }
        if (choices.isEmpty()) return null;
        return choices.get(Minecraft.getInstance().level == null
                ? (int) (Math.random() * choices.size())
                : Minecraft.getInstance().level.random.nextInt(choices.size()));
    }

    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get().resolve(LaowuMemeMod.MOD_ID);
    }

    public static Path getSoundsDir() {
        return getConfigDir().resolve("sounds");
    }

    /** 只允许解析到 sounds 目录的直接子文件，避免构造路径逃逸。 */
    public static Path resolveImportedFile(String name) {
        Path directory = getSoundsDir().toAbsolutePath().normalize();
        Path file = directory.resolve(name + ".ogg").normalize();
        if (!file.getParent().equals(directory)) {
            throw new IllegalArgumentException("Invalid imported sound name");
        }
        return file;
    }

    private static boolean hasBuiltinOgg(String name) {
        ResourceLocation file = new ResourceLocation(LaowuMemeMod.MOD_ID, "sounds/" + name + ".ogg");
        return Minecraft.getInstance().getResourceManager().getResource(file).isPresent();
    }

    private static void persist() {
        Map<String, Boolean> state = new LinkedHashMap<>();
        BUILTINS.keySet().forEach(key -> state.put("builtin:" + key, isEnabled("builtin:" + key)));
        imported.forEach(name -> state.put("imported:" + name, isEnabled("imported:" + name)));
        EnabledConfig.save(state);
    }

    private static String stripExtension(String name) {
        return name.substring(0, name.length() - 4);
    }

    public record PlayTarget(SoundEvent event, String importedName) {
        public static PlayTarget builtin(SoundEvent event) {
            return new PlayTarget(event, null);
        }

        public static PlayTarget imported(String name) {
            return new PlayTarget(null, name);
        }

        public boolean isImported() {
            return importedName != null;
        }
    }
}
