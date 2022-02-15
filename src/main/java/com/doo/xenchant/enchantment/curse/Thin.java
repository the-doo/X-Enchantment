package com.doo.xenchant.enchantment.curse;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

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
    public void itemUsedCallback(LivingEntity owner, ItemStack stack, Integer level, float amount) {
        stack.setDamage(stack.getDamage() + level * 2);
    }
}
