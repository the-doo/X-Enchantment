package com.doo.xenchant.enchantment.curse;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Thin
 */
public class Thin extends Cursed {

    public static final String NAME = "thin";

    public Thin() {
        super(NAME, Rarity.COMMON, EnchantmentTarget.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public int getMinPower(int level) {
        return 1 + (level - 1) * 10;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void itemUsedCallback(@Nullable LivingEntity owner, ItemStack stack, Integer level, float amount) {
        if (owner == null) {
            return;
        }

        if (owner.getRandom().nextBoolean() && owner.getRandom().nextBoolean()) {
            stack.setDamage(stack.getDamage() + level * 2);
        }
    }
}
