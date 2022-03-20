package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.S2CFishCaughtCallback;
import com.doo.xenchant.util.EnchantUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 自动钓鱼
 */
public class AutoFish extends BaseEnchantment {

    public static final String NAME = "auto_fish";

    public AutoFish() {
        super(NAME, Enchantment.Rarity.COMMON, EnchantmentCategory.FISHING_ROD, EnchantUtil.ALL_HAND);
    }

    @Override
    public void register() {
        super.register();

        S2CFishCaughtCallback.EVENT.register(getId(), player -> {
            // check enchantment
            if (!Enchant.option.autoFishing) {
                return;
            }

            ItemStack stack = EnchantUtil.getHandStack(player, FishingRodItem.class, s -> level(s) > 0);
            if (stack == null || stack.isEmpty()) {
                return;
            }

            // 25% chance to return 10 damage
            if (player.getRandom().nextBoolean() && player.getRandom().nextBoolean()) {
                stack.setDamageValue(stack.getDamageValue() - 10);
            }

            ServerPlayNetworking.send((ServerPlayer) player, getId(), PacketByteBufs.create());
        });

        // client register
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(getId(), ((client, handler, buf, responseSender) -> {
                LocalPlayer player = client.player;
                if (player == null) {
                    return;
                }

                // right click
                InputConstants.Key key = InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_RIGHT);
                KeyMapping.click(key);
                // and right click again
                client.execute(() -> KeyMapping.click(key));
            }));
        }
    }
}
