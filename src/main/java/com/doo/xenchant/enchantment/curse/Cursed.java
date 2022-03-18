package com.doo.xenchant.enchantment.curse;

import com.doo.xenchant.enchantment.BaseEnchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

/**
 * Cursed Enchantment
 */
public abstract class Cursed extends BaseEnchantment {

    protected Cursed(String name, Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(name, weight, type, slotTypes);
    }

    @Override
    public boolean isCursed() {
        return true;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }
}
