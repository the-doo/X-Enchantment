package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.S2CFishCaughtCallback;
import com.doo.xenchant.util.EnchantUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * 自动钓鱼
 */
public class AutoFish extends BaseEnchantment {

    public static final String NAME = "auto_fish";

    public AutoFish() {
        super(new Identifier(Enchant.ID, NAME), Enchantment.Rarity.COMMON, EnchantmentTarget.FISHING_ROD,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMinPower(int level) {
        return 20;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public void register() {
        super.register();

        // server register
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            S2CFishCaughtCallback.EVENT.register(getId(), ((player, item) -> {
                // check enchantment
                if (level(item) < 1) {
                    return;
                }

                // 50% chance to add 2 damage
                if (player.getRandom().nextBoolean()) {
                    item.setDamage(item.getDamage() + 2);
                }

                ServerPlayNetworking.send((ServerPlayerEntity) player, getId(), PacketByteBufs.create());
            }));
        }

        // client register
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(getId(), ((client, handler, buf, responseSender) -> {
                ClientPlayerEntity player = client.player;
                if (player == null) {
                    return;
                }

                // right click
                KeyBinding.onKeyPressed(EnchantUtil.MOUSE_RIGHT_CLICK);
                // and right click again
                client.execute(() -> KeyBinding.onKeyPressed(EnchantUtil.MOUSE_RIGHT_CLICK));
            }));
        }

    }
}
