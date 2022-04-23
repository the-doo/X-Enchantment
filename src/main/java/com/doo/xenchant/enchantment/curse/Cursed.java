package com.doo.xenchant.enchantment.curse;

import com.doo.xenchant.enchantment.BaseEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Cursed Enchantment
 */
public abstract class Cursed extends BaseEnchantment {

    protected Cursed(String name, Rarity weight, EnchantmentCategory type, EquipmentSlot[] slotTypes) {
        super(name, weight, type, slotTypes);
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return true;
    }
}
