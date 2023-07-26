package com.doo.xenchantment.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ServersideChannelUtil {

    private ServersideChannelUtil() {
    }


    public interface Sender {

        void send(ServerPlayer player, ResourceLocation id, FriendlyByteBuf friendlyByteBuf);
    }

    public static Sender sender;

    public static void send(ServerPlayer player, ResourceLocation id, FriendlyByteBuf friendlyByteBuf) {
        sender.send(player, id, friendlyByteBuf);
    }

    public static void setSender(Sender sender) {
        ServersideChannelUtil.sender = sender;
    }
}
