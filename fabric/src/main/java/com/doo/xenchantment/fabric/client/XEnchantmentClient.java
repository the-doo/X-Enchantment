package com.doo.xenchantment.fabric.client;

import com.doo.xenchantment.enchantment.AutoFish;
import com.doo.xenchantment.util.ClientsideChannelUtil;
import com.doo.xenchantment.util.EnchantUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class XEnchantmentClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EnchantUtil.ENCHANTMENTS_MAP.values().stream().filter(AutoFish.class::isInstance).findFirst()
                .ifPresent(e -> ClientPlayNetworking.registerGlobalReceiver(e.getId(), ((client, handler, buf, responseSender) -> ClientsideChannelUtil.autoFish(client))));

        EnchantUtil.registerToolTips(e -> ItemTooltipCallback.EVENT.register(e::tooltip));

        EnchantUtil.onClient();
    }
}
