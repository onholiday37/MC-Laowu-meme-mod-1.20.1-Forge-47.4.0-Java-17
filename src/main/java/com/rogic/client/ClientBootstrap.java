package com.rogic.client;

import com.rogic.LaowuMemeMod;
import com.rogic.client.sound.AudioPool;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

/** 只在物理客户端加载的初始化入口。 */
public final class ClientBootstrap {
    private ClientBootstrap() {}

    public static void init() {
        AudioPool.init();
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(LaowuConfigScreen::new));
        LaowuMemeMod.LOGGER.info("[laowu meme] Forge client initialized");
    }
}
