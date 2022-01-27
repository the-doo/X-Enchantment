package com.doo.xenchant.enchantment;

import com.doo.xenchant.events.S2CFishCaughtCallback;
import com.doo.xenchant.util.EnchantUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.server.network.ServerPlayerEntity;
import org.lwjgl.glfw.GLFW;

/**
 * 自动钓鱼
 */
public class AutoFish extends BaseEnchantment {

    public static final String NAME = "auto_fish";

    private static final InputUtil.Key MOUSE_RIGHT_CLICK = InputUtil.Type.MOUSE.createFromCode(GLFW.GLFW_MOUSE_BUTTON_RIGHT);

    public AutoFish() {
        super(NAME, Enchantment.Rarity.COMMON, EnchantmentTarget.FISHING_ROD, EnchantUtil.ALL_HAND);
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

        S2CFishCaughtCallback.EVENT.register(getId(), ((player, item) -> {
            // check enchantment
            if (item.isEmpty() || level(item) < 1) {
                return;
            }

            // 25% chance to return 10 damage
            if (player.getRandom().nextBoolean() && player.getRandom().nextBoolean()) {
                item.setDamage(item.getDamage() - 10);
            }

            ServerPlayNetworking.send((ServerPlayerEntity) player, getId(), PacketByteBufs.create());
        }));

        // client register
        ClientPlayNetworking.registerGlobalReceiver(getId(), ((client, handler, buf, responseSender) -> {
            ClientPlayerEntity player = client.player;
            if (player == null) {
                return;
            }

            // right click
            KeyBinding.onKeyPressed(MOUSE_RIGHT_CLICK);
            // and right click again
            client.execute(() -> KeyBinding.onKeyPressed(MOUSE_RIGHT_CLICK));
        }));

    }
}
