package com.doo.xenchant.enchantment.curse;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

/**
 * DownDamage
 */
public class DownDamage extends Cursed {

    public static final String NAME = "down_damage";

    public DownDamage() {
        super(NAME, Rarity.COMMON, EnchantmentTarget.WEAPON, EquipmentSlot.values());
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
        return 5;
    }

    @Override
    public float getMultiTotalDamage(LivingEntity attacker, LivingEntity target, ItemStack stack, int level) {
        return -(0.1F * level);
    }
}
