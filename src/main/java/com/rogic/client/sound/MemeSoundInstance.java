package com.rogic.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

/** 内置音乐实例：循环播放、跟随两只猫中点，并在 16 格内平滑衰减。 */
public final class MemeSoundInstance extends AbstractTickableSoundInstance {
    private final int catAId;
    private final int catBId;

    public MemeSoundInstance(SoundEvent sound, int catAId, int catBId) {
        super(sound, SoundSource.NEUTRAL, RandomSource.create());
        this.catAId = catAId;
        this.catBId = catBId;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0F;
        this.attenuation = SoundInstance.Attenuation.NONE;
        updatePosition();
    }

    @Override
    public float getVolume() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return volume;
        double distance = Math.sqrt(minecraft.player.distanceToSqr(x, y, z));
        return volume * Math.max(0.0F, Math.min(1.0F, (float) (1.0D - distance / 16.0D)));
    }

    @Override
    public void tick() {
        if (!updatePosition()) stop();
    }

    private boolean updatePosition() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return false;
        Entity catA = minecraft.level.getEntity(catAId);
        Entity catB = minecraft.level.getEntity(catBId);
        if (catA == null || catB == null) return false;
        x = (catA.getX() + catB.getX()) * 0.5D;
        y = (catA.getY() + catB.getY()) * 0.5D;
        z = (catA.getZ() + catB.getZ()) * 0.5D;
        return minecraft.player == null || minecraft.player.distanceToSqr(x, y, z) <= 32.0D * 32.0D;
    }
}
