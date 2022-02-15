package com.doo.xenchant.enchantment.curse;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

/**
 * DownArmor
 */
public class DownArmor extends Cursed {

    public static final String NAME = "down_armor";

    public DownArmor() {
        super(NAME, Rarity.COMMON, EnchantmentTarget.ARMOR, EquipmentSlot.values());
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
    public int getProtectionAmount(int level, DamageSource source) {
        return super.getProtectionAmount(level, source);
    }

    @Override
    public float getMultiTotalArmor(LivingEntity living, float damage, ItemStack stack, Integer level) {
        return -(0.1F * level);
    }
}
