package com.rogic.network;

import com.rogic.client.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 服务端通知客户端：两只猫进入整活状态。 */
public record MemeTriggerS2CPacket(int catAId, int catBId, int soundId, int rollSign) {
    public static void encode(MemeTriggerS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.catAId);
        buf.writeVarInt(packet.catBId);
        buf.writeVarInt(packet.soundId);
        buf.writeInt(packet.rollSign);
    }

    public static MemeTriggerS2CPacket decode(FriendlyByteBuf buf) {
        return new MemeTriggerS2CPacket(buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readInt());
    }

    public static void handle(MemeTriggerS2CPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientPacketHandler.handleTrigger(packet)));
        context.setPacketHandled(true);
    }
}
