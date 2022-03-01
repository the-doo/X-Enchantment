package com.doo.xenchant.enchantment;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

/**
 * 命中率提升
 */
public class HitRateUp extends BaseEnchantment {

    public static final String NAME = "hit_rate_up";

    public HitRateUp() {
        super(NAME, Rarity.UNCOMMON, EnchantmentTarget.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 20;
    }

    @Override
    public int getMaxPower(int level) {
        return level * 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
