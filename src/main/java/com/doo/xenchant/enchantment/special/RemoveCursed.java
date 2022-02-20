package com.doo.xenchant.enchantment.special;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.Map;

/**
 * Remove Cursed
 */
public class RemoveCursed extends Special {

    public static final String NAME = "remove_cursed";

    public RemoveCursed() {
        super(NAME, Rarity.RARE, EnchantmentTarget.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public int getMinPower(int level) {
        return 20;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }

    @Override
    public void onAnvil(Map<Enchantment, Integer> map, int level, ItemStack stack) {
        // remove this
        map.remove(this);

        // If map has cursed remove First
        map.keySet().stream().filter(Enchantment::isCursed).findFirst()
                .ifPresent(map::remove);
    }
}
