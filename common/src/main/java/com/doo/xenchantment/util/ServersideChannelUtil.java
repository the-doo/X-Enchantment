package com.doo.xenchantment.util;

import com.google.gson.JsonObject;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ServersideChannelUtil {

    private ServersideChannelUtil() {
    }

    public static FriendlyByteBuf getJsonBuf(JsonObject json) {
        return getJsonBuf(json, new FriendlyByteBuf(Unpooled.buffer()));
    }

    public static FriendlyByteBuf getJsonBuf(JsonObject json, FriendlyByteBuf buf) {
        byte[] bytes = json.toString().getBytes();
        buf.capacity(bytes.length);
        buf.writeBytes(bytes);
        return buf;
    }


    public interface Sender {

        void send(ServerPlayer player, ResourceLocation id, FriendlyByteBuf friendlyByteBuf, Object forgeSource);
    }

    private static Sender sender;

    public static void send(ServerPlayer player, ResourceLocation id, FriendlyByteBuf friendlyByteBuf, Object forgeSource) {
        sender.send(player, id, friendlyByteBuf, forgeSource);
    }

    public static void setSender(Sender sender) {
        ServersideChannelUtil.sender = sender;
    }
}
