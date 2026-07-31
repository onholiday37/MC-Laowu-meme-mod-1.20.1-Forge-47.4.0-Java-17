package com.rogic.client;

import com.rogic.client.sound.AudioPool;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** 无第三方依赖的轻量音频设置页。 */
public final class LaowuConfigScreen extends Screen {
    private final Screen parent;
    private int page;
    private int pageCount = 1;
    private Component status = Component.empty();

    public LaowuConfigScreen(Screen parent) {
        super(Component.translatable("screen.laowu_meme.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        AudioPool.init();
        AudioPool.refreshImported();

        int centerX = width / 2;
        int rowsPerPage = Math.max(1, (height - 142) / 24);
        List<AudioEntry> entries = collectEntries();
        pageCount = Math.max(1, (entries.size() + rowsPerPage - 1) / rowsPerPage);
        page = Math.max(0, Math.min(page, pageCount - 1));

        int first = page * rowsPerPage;
        int last = Math.min(first + rowsPerPage, entries.size());
        int buttonWidth = Math.min(360, width - 40);
        int y = 54;
        for (int index = first; index < last; index++) {
            AudioEntry entry = entries.get(index);
            Button button = Button.builder(entry.label(), pressed -> {
                        boolean enabled = AudioPool.toggleEnabled(entry.key());
                        pressed.setMessage(toggleLabel(entry.name(), enabled));
                    })
                    .bounds(centerX - buttonWidth / 2, y, buttonWidth, 20)
                    .build();
            button.active = entry.available();
            addRenderableWidget(button);
            y += 24;
        }

        int navigationY = height - 78;
        Button previous = addRenderableWidget(Button.builder(
                        Component.translatable("screen.laowu_meme.previous"),
                        pressed -> changePage(-1))
                .bounds(centerX - 154, navigationY, 100, 20).build());
        previous.active = page > 0;

        addRenderableWidget(Button.builder(
                        Component.translatable("screen.laowu_meme.refresh"),
                        pressed -> refreshScreen())
                .bounds(centerX - 50, navigationY, 100, 20).build());

        Button next = addRenderableWidget(Button.builder(
                        Component.translatable("screen.laowu_meme.next"),
                        pressed -> changePage(1))
                .bounds(centerX + 54, navigationY, 100, 20).build());
        next.active = page + 1 < pageCount;

        int actionY = height - 52;
        addRenderableWidget(Button.builder(
                        Component.translatable("screen.laowu_meme.open_folder"),
                        pressed -> openFolder())
                .bounds(centerX - 154, actionY, 100, 20).build());
        addRenderableWidget(Button.builder(
                        Component.translatable("screen.laowu_meme.copy_path"),
                        pressed -> copyPath())
                .bounds(centerX - 50, actionY, 100, 20).build());
        addRenderableWidget(Button.builder(
                        Component.translatable("gui.done"),
                        pressed -> onClose())
                .bounds(centerX + 54, actionY, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 12, 0xFFFFFF);
        graphics.drawCenteredString(font,
                Component.translatable("screen.laowu_meme.hint"), width / 2, 30, 0xAAAAAA);
        graphics.drawCenteredString(font,
                Component.translatable("screen.laowu_meme.page", page + 1, pageCount),
                width / 2, height - 91, 0xAAAAAA);
        if (!status.getString().isEmpty()) {
            graphics.drawCenteredString(font, status, width / 2, height - 14, 0x55FF55);
        }
    }

    @Override
    public void onClose() {
        if (minecraft != null) minecraft.setScreen(parent);
    }

    private List<AudioEntry> collectEntries() {
        List<AudioEntry> entries = new ArrayList<>();
        for (String name : AudioPool.builtinKeys()) {
            boolean available = AudioPool.isBuiltinAvailable(name);
            Component label = available
                    ? toggleLabel(AudioPool.BUILTIN_DISPLAY.get(name), AudioPool.isEnabled("builtin:" + name))
                    : Component.translatable("screen.laowu_meme.missing_builtin", AudioPool.BUILTIN_DISPLAY.get(name));
            entries.add(new AudioEntry("builtin:" + name, AudioPool.BUILTIN_DISPLAY.get(name), label, available));
        }
        for (String key : AudioPool.importedKeys()) {
            String name = key.substring("imported:".length());
            entries.add(new AudioEntry(key, "[OGG] " + name,
                    toggleLabel("[OGG] " + name, AudioPool.isEnabled(key)), true));
        }
        return entries;
    }

    private static Component toggleLabel(String name, boolean enabled) {
        return Component.literal((enabled ? "✓ " : "✗ ") + name)
                .withStyle(style -> style.withColor(enabled ? 0x55FF55 : 0xFF7777));
    }

    private void changePage(int delta) {
        page += delta;
        refreshScreen();
    }

    private void refreshScreen() {
        if (minecraft != null) {
            LaowuConfigScreen replacement = new LaowuConfigScreen(parent);
            replacement.page = page;
            replacement.status = status;
            minecraft.setScreen(replacement);
        }
    }

    private void openFolder() {
        Path directory = AudioPool.getSoundsDir();
        AudioPool.refreshImported();
        Util.getPlatform().openFile(directory.toFile());
        status = Component.translatable("screen.laowu_meme.folder_opened");
    }

    private void copyPath() {
        if (minecraft == null) return;
        minecraft.keyboardHandler.setClipboard(AudioPool.getSoundsDir().toAbsolutePath().toString());
        status = Component.translatable("screen.laowu_meme.path_copied");
    }

    private record AudioEntry(String key, String name, Component label, boolean available) {}
}
