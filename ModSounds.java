package com.rogic.client;

import com.rogic.network.MemeStopS2CPacket;
import com.rogic.network.MemeTriggerS2CPacket;

/** 把网络线程收到的状态转交给客户端状态容器。 */
public final class ClientPacketHandler {
    private ClientPacketHandler() {}

    public static void handleTrigger(MemeTriggerS2CPacket packet) {
        ClientMemeState.get().onTrigger(packet.catAId(), packet.catBId(), packet.soundId(), packet.rollSign());
    }

    public static void handleStop(MemeStopS2CPacket packet) {
        ClientMemeState.get().onStop(packet.catAId(), packet.catBId());
    }
}
