package com.doo.xenchantment.enchantment.curse;

import com.doo.xenchantment.enchantment.BaseXEnchantment;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Cursed Enchantment
 */
public abstract class Cursed extends BaseXEnchantment {

    protected Cursed(String name, Rarity weight, EnchantmentCategory type, EquipmentSlot... slotTypes) {
        super(name, weight, type, slotTypes);
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Override
    public ChatFormatting optionsTextColor() {
        return ChatFormatting.RED;
    }
}
