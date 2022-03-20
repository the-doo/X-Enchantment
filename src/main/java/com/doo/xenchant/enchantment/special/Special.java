package com.doo.xenchant.enchantment.special;

import com.doo.xenchant.enchantment.BaseEnchantment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * It's special enchantment, maybe you don't like it
 */
public abstract class Special extends BaseEnchantment {

    protected Special(String name, Rarity weight, EnchantmentCategory type, EquipmentSlot[] slotTypes) {
        super(name, weight, type, slotTypes);
    }

    @Override
    public Component getFullname(int level) {
        return super.getFullname(level).copy().withStyle(ChatFormatting.DARK_PURPLE);
    }


    @Override
    public final boolean isTradeable() {
        return true;
    }
}
