package com.rogic;

import com.mojang.logging.LogUtils;
import com.rogic.client.ClientBootstrap;
import com.rogic.network.ModNetworking;
import com.rogic.sound.ModSounds;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Cat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/** Forge 1.20.1 主入口。 */
@Mod(LaowuMemeMod.MOD_ID)
public final class LaowuMemeMod {
    public static final String MOD_ID = "laowu_meme";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LaowuMemeMod() {
        ModSounds.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModNetworking.register();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientBootstrap::init);
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("[laowu meme] Forge 1.20.1 common initialized");
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ServerMemeManager.serverTick(event.getServer());
        }
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide() || !(event.getTarget() instanceof Cat cat)) return;
        InteractionResult result = ServerMemeManager.onRightClick(cat);
        if (result.consumesAction()) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        ServerMemeManager.clear();
    }
}
