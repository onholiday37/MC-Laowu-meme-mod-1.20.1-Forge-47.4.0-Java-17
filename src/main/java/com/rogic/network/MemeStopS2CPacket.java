package com.rogic.network;

import com.rogic.client.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** 服务端通知客户端：两只猫结束整活状态。 */
public record MemeStopS2CPacket(int catAId, int catBId) {
    public static void encode(MemeStopS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.catAId);
        buf.writeVarInt(packet.catBId);
    }

    public static MemeStopS2CPacket decode(FriendlyByteBuf buf) {
        return new MemeStopS2CPacket(buf.readVarInt(), buf.readVarInt());
    }

    public static void handle(MemeStopS2CPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientPacketHandler.handleStop(packet)));
        context.setPacketHandled(true);
    }
}
