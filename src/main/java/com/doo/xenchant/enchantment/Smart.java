package com.doo.xenchant.enchantment;

import com.doo.xenchant.events.LivingApi;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Smart
 */
public class Smart extends BaseEnchantment {

    public static final String NAME = "smart";

    public Smart() {
        super(NAME, Rarity.VERY_RARE, EnchantmentTarget.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public void register() {
        super.register();

        LivingApi.SEVER_TAIL_TICK.register(living -> {
            if (!(living instanceof ServerPlayerEntity) || living.age % (SECOND * 5) != 0) {
                return;
            }

            ItemStack stack = living.getEquippedStack(EquipmentSlot.HEAD);
            if (stack.isEmpty()) {
                return;
            }

            int level = level(stack);
            if (level < 1) {
                return;
            }

            // add level xp
            int amount = level;
            // if epiphany - 0.0005
            if (living.getRandom().nextInt(1000) < 5) {
                amount *= 1000;
            }

            ((PlayerEntity) living).addExperience(amount);
        });
    }
}
