package com.doo.xenchantment.enchantment.special;

import com.doo.xenchantment.enchantment.BaseXEnchantment;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

/**
 * It's special enchantment, maybe you don't like it
 */
public abstract class Special extends BaseXEnchantment {

    protected Special(String name, EnchantmentCategory type, EquipmentSlot... slotTypes) {
        super(name, Rarity.UNCOMMON, type, slotTypes);
    }

    @Override
    public @NotNull Component getFullname(int level) {
        return super.getFullname(level).copy().withStyle(ChatFormatting.DARK_PURPLE);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    protected void resetLevel(Integer level, ItemStack stack, ListTag tag) {
        if (level == 1) {
            tag.removeIf(this::isSameId);
            if (tag.isEmpty()) {
                stack.setCount(0);
            }
        } else {
            tag.stream().filter(this::isSameId).findFirst()
                    .ifPresent(t -> EnchantmentHelper.setEnchantmentLevel((CompoundTag) t, level - 1));
        }
    }

    @Override
    public ChatFormatting optionsTextColor() {
        return ChatFormatting.DARK_PURPLE;
    }
}
