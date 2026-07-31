package com.rogic.client.sound;

import com.rogic.LaowuMemeMod;
import com.rogic.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

/** 外部 OGG 实例；虚拟资源路径由 SoundBufferLibraryMixin 映射回磁盘文件。 */
public final class ImportedSoundInstance extends AbstractTickableSoundInstance {
    private final WeighedSoundEvents events;
    private final int catAId;
    private final int catBId;

    public ImportedSoundInstance(String baseName, int catAId, int catBId) {
        // 这里只借用一个已注册事件满足父类构造；resolve() 会换成下方磁盘声音。
        super(ModSounds.LAOWU2.get(), SoundSource.NEUTRAL, RandomSource.create());
        Sound diskSound = new Sound(
                LaowuMemeMod.MOD_ID + ":imported/" + SoundIdCodec.encode(baseName),
                random -> 1.0F,
                random -> 1.0F,
                1,
                Sound.Type.FILE,
                true,
                false,
                16);
        this.events = new WeighedSoundEvents(getLocation(), null);
        this.events.addSound(diskSound);
        this.catAId = catAId;
        this.catBId = catBId;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0F;
        this.attenuation = SoundInstance.Attenuation.NONE;
        updatePosition();
    }

    @Override
    public WeighedSoundEvents resolve(SoundManager manager) {
        this.sound = events.getSound(random);
        return events;
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
