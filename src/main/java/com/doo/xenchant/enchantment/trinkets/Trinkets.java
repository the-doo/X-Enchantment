package com.doo.xenchant.enchantment.trinkets;

import com.doo.xenchant.enchantment.BaseEnchantment;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * It's trinkets enchantment, maybe you don't like it
 */
public abstract class Trinkets extends BaseEnchantment {

    protected Trinkets(String name) {
        super(name, Rarity.UNCOMMON, EnchantmentTarget.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public final Text getName(int level) {
        return super.getName(level).shallowCopy().formatted(Formatting.GREEN);
    }

    @Override
    public final boolean isTreasure() {
        return true;
    }

    @Override
    public final boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof TrinketItem && !stack.hasEnchantments();
    }

    @Override
    protected final boolean canAccept(Enchantment other) {
        return false;
    }
}
