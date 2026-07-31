package com.rogic.sound;

import com.rogic.LaowuMemeMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Forge 声音事件注册。实际 OGG 是否存在由客户端音频池在播放前检查。 */
public final class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, LaowuMemeMod.MOD_ID);

    public static final RegistryObject<SoundEvent> LAOWU2 = register("laowu2");
    public static final RegistryObject<SoundEvent> QILIANG = register("qiliang");
    public static final RegistryObject<SoundEvent> ZHANHOU = register("zhanhou");

    private ModSounds() {}

    private static RegistryObject<SoundEvent> register(String name) {
        ResourceLocation id = new ResourceLocation(LaowuMemeMod.MOD_ID, name);
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus modBus) {
        SOUNDS.register(modBus);
    }
}
