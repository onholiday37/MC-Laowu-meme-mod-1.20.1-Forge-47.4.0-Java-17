package com.rogic.network;

import com.rogic.LaowuMemeMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetworking {
    private static final String PROTOCOL = "1";
    private static int nextId;

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(LaowuMemeMod.MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private ModNetworking() {}

    public static void register() {
        CHANNEL.registerMessage(nextId++, MemeTriggerS2CPacket.class,
                MemeTriggerS2CPacket::encode, MemeTriggerS2CPacket::decode, MemeTriggerS2CPacket::handle);
        CHANNEL.registerMessage(nextId++, MemeStopS2CPacket.class,
                MemeStopS2CPacket::encode, MemeStopS2CPacket::decode, MemeStopS2CPacket::handle);
    }

    public static void sendToAll(Object packet) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
    }
}
