package com.doo.xenchant.enchantment;

import com.doo.xenchant.events.LivingApi;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Smart
 */
public class Smart extends BaseEnchantment {

    public static final String NAME = "smart";

    public Smart() {
        super(NAME, Rarity.VERY_RARE, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isTradeable() {
        return true;
    }

    @Override
    public void register() {
        super.register();

        LivingApi.SEVER_TAIL_TICK.register(living -> {
            if (!(living instanceof ServerPlayer) || living.tickCount % (SECOND * 5) != 0) {
                return;
            }

            ItemStack stack = living.getItemBySlot(EquipmentSlot.HEAD);
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

            ((Player) living).giveExperienceLevels(amount);
        });
    }
}
