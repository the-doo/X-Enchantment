package com.doo.xenchant.enchantment.special;

import com.doo.xenchant.enchantment.BaseEnchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * It's special enchantment, maybe you don't like it
 */
public abstract class Special extends BaseEnchantment {

    protected Special(String name, Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(name, weight, type, slotTypes);
    }

    @Override
    public final int getMaxLevel() {
        return super.getMaxLevel();
    }

    @Override
    public Text getName(int level) {
        return super.getName(level).shallowCopy().formatted(Formatting.DARK_PURPLE);
    }
}
