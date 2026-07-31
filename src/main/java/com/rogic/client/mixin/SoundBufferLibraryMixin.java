package com.rogic.client.mixin;

import com.mojang.blaze3d.audio.OggAudioStream;
import com.rogic.LaowuMemeMod;
import com.rogic.client.sound.AudioPool;
import com.rogic.client.sound.SoundIdCodec;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.LoopingAudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/** 把 laowu_meme:sounds/imported/<hex>.ogg 映射到 config/laowu_meme/sounds。 */
@Mixin(SoundBufferLibrary.class)
public abstract class SoundBufferLibraryMixin {
    private static final String PREFIX = "sounds/imported/";

    @Inject(
            method = "getStream(Lnet/minecraft/resources/ResourceLocation;Z)Ljava/util/concurrent/CompletableFuture;",
            at = @At("HEAD"),
            cancellable = true)
    private void laowuMeme$getImportedStream(
            ResourceLocation location,
            boolean looping,
            CallbackInfoReturnable<CompletableFuture<AudioStream>> callback) {
        if (!LaowuMemeMod.MOD_ID.equals(location.getNamespace())) return;
        String path = location.getPath();
        if (!path.startsWith(PREFIX) || !path.endsWith(".ogg")) return;

        String encoded = path.substring(PREFIX.length(), path.length() - 4);
        try {
            String fileName = SoundIdCodec.decode(encoded);
            Path file = AudioPool.resolveImportedFile(fileName);
            if (!Files.isRegularFile(file)) return;

            InputStream input = Files.newInputStream(file);
            AudioStream stream;
            if (looping) {
                LoopingAudioStream.AudioStreamProvider provider = OggAudioStream::new;
                stream = new LoopingAudioStream(provider, input);
            } else {
                stream = new OggAudioStream(input);
            }
            callback.setReturnValue(CompletableFuture.completedFuture(stream));
        } catch (IOException | IllegalArgumentException exception) {
            LaowuMemeMod.LOGGER.warn("[laowu meme] 外部 OGG 读取失败：{}", location, exception);
        }
    }
}
