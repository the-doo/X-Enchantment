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
import net.minecraft.client.util.InputUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.lwjgl.glfw.GLFW;

/**
 * 自动钓鱼
 */
public class AutoFish extends BaseEnchantment {

    public static final String NAME = "auto_fish";

    public AutoFish() {
        super(NAME, Enchantment.Rarity.COMMON, EnchantmentTarget.FISHING_ROD, EnchantUtil.ALL_HAND);
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
                stack.setDamage(stack.getDamage() - 10);
            }

            ServerPlayNetworking.send((ServerPlayerEntity) player, getId(), PacketByteBufs.create());
        });

        // client register
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(getId(), ((client, handler, buf, responseSender) -> {
                ClientPlayerEntity player = client.player;
                if (player == null) {
                    return;
                }

                // right click
                InputUtil.Key key = InputUtil.Type.MOUSE.createFromCode(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
                KeyBinding.onKeyPressed(key);
                // and right click again
                client.execute(() -> KeyBinding.onKeyPressed(key));
            }));
        }
    }
}
