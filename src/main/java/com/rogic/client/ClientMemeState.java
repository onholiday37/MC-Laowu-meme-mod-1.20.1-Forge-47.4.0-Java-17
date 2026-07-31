package com.rogic.client;

import com.rogic.LaowuMemeMod;
import com.rogic.client.sound.AudioPool;
import com.rogic.client.sound.ImportedSoundInstance;
import com.rogic.client.sound.MemeSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;

import java.util.HashMap;
import java.util.Map;

/** 客户端只保存服务端同步来的渲染状态。 */
public final class ClientMemeState {
    private static final ClientMemeState INSTANCE = new ClientMemeState();

    public static ClientMemeState get() {
        return INSTANCE;
    }

    private record ActiveCat(int partnerId, int soundId, int rollSign) {}

    private final Map<Integer, ActiveCat> activeCats = new HashMap<>();
    private final Map<String, SoundInstance> activeSounds = new HashMap<>();

    private ClientMemeState() {}

    public boolean isActive(int entityId) {
        return activeCats.containsKey(entityId);
    }

    public int getRollSign(int entityId) {
        ActiveCat state = activeCats.get(entityId);
        return state == null ? 0 : state.rollSign();
    }

    public void onTrigger(int catAId, int catBId, int soundId, int rollSign) {
        activeCats.put(catAId, new ActiveCat(catBId, soundId, rollSign));
        // 两只猫身体朝向相反，因此使用相同局部 roll 会呈现面对面的镜像歪头。
        activeCats.put(catBId, new ActiveCat(catAId, soundId, rollSign));
        startSound(catAId, catBId);
    }

    public void onStop(int catAId, int catBId) {
        activeCats.remove(catAId);
        activeCats.remove(catBId);
        stopSound(catAId, catBId);
    }

    public void clear() {
        activeCats.clear();
        Minecraft minecraft = Minecraft.getInstance();
        activeSounds.values().forEach(minecraft.getSoundManager()::stop);
        activeSounds.clear();
    }

    private void startSound(int catAId, int catBId) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) return;

        stopSound(catAId, catBId);
        AudioPool.PlayTarget target = AudioPool.random();
        if (target == null) {
            LaowuMemeMod.LOGGER.debug("[laowu meme] 没有可播放的 OGG；请放入 {}", AudioPool.getSoundsDir());
            return;
        }

        SoundInstance sound = target.isImported()
                ? new ImportedSoundInstance(target.importedName(), catAId, catBId)
                : new MemeSoundInstance(target.event(), catAId, catBId);
        activeSounds.put(pairKey(catAId, catBId), sound);
        minecraft.getSoundManager().play(sound);
    }

    private void stopSound(int catAId, int catBId) {
        SoundInstance sound = activeSounds.remove(pairKey(catAId, catBId));
        if (sound != null) Minecraft.getInstance().getSoundManager().stop(sound);
    }

    private static String pairKey(int first, int second) {
        return Math.min(first, second) + "-" + Math.max(first, second);
    }
}
