package com.doo.xenchantment.enchantment.special;

import com.doo.xenchantment.enchantment.BaseXEnchantment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

/**
 * It's special enchantment, maybe you don't like it
 */
public abstract class Special extends BaseXEnchantment {

    protected Special(String name, EnchantmentCategory type, EquipmentSlot[] slotTypes) {
        super(name, Rarity.UNCOMMON, type, slotTypes);
    }

    @Override
    public @NotNull Component getFullname(int level) {
        return super.getFullname(level).copy().withStyle(ChatFormatting.DARK_PURPLE);
    }
}
