package com.doo.xenchant.enchantment;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;

/**
 * King Kong Legs
 */
public class KingKongLegs extends BaseEnchantment {

    public static final String NAME = "king_kong_legs";

    public KingKongLegs() {
        super(NAME, Rarity.UNCOMMON, EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[]{EquipmentSlot.LEGS});
    }

    @Override
    public int getMinPower(int level) {
        return 50;
    }

    @Override
    public int getProtectionAmount(int level, DamageSource source) {
        return !source.isOutOfWorld() && source.isFromFalling() ? Integer.MAX_VALUE : 0;
    }
}
