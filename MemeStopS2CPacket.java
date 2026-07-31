package com.rogic.client;

import com.rogic.LaowuMemeMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** 客户端世界切换时清理旧实体 id，防止状态串到下一世界。 */
@Mod.EventBusSubscriber(modid = LaowuMemeMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientRenderEvents {
    private ClientRenderEvents() {}

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientMemeState.get().clear();
    }
}
