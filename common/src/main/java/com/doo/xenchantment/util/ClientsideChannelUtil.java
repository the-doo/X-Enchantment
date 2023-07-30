package com.doo.xenchantment.util;

import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.FishingRodItem;

public class ClientsideChannelUtil {

    private ClientsideChannelUtil() {
    }

    public static void loadConfig(ByteBuf buf) {
        loadConfig(getConfig(buf, 0));
    }

    public static void loadConfig(JsonObject buf) {
        EnchantUtil.configLoad(buf);
    }

    public static JsonObject getConfig(ByteBuf buf, int start) {
        return getConfig(buf.array(), start, buf.capacity() - 1);
    }

    public static JsonObject getConfig(byte[] array, int start, int len) {
        return ConfigUtil.JSON.fromJson(new String(array, start, len), JsonObject.class);
    }

    public static void autoFish(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }

        if (player.fishing == null) {
            return;
        }

        if (player.getMainHandItem().getItem() instanceof FishingRodItem) {
            client.gameMode.useItem(player, InteractionHand.MAIN_HAND);
            // and right click again
            client.execute(() -> client.gameMode.useItem(player, InteractionHand.MAIN_HAND));
            return;
        }
        if (player.getOffhandItem().getItem() instanceof FishingRodItem) {
            client.gameMode.useItem(player, InteractionHand.OFF_HAND);
            // and right click again
            client.execute(() -> client.gameMode.useItem(player, InteractionHand.OFF_HAND));
        }
    }
}
