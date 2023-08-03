package com.doo.xenchantment.util;

import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;

public class ClientsideChannelUtil {

    private ClientsideChannelUtil() {
    }

    public static void loadConfig(ByteBuf buf) {
        loadConfig(ServersideChannelUtil.getConfig(buf));
    }

    public static void loadConfig(JsonObject json) {
        EnchantUtil.configLoad(json);
    }

    public static void autoFish() {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
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
