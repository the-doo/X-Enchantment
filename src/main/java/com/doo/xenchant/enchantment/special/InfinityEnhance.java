package com.doo.xenchant.enchantment.special;

import com.doo.xenchant.events.AnvilApi;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

/**
 * Infinity Enhance
 */
public class InfinityEnhance extends Special {

    public static final String NAME = "infinity_enhance";

    public InfinityEnhance() {
        super(NAME, Rarity.RARE, EnchantmentTarget.BOW, EquipmentSlot.values());
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return EnchantmentHelper.get(stack).containsKey(Enchantments.INFINITY);
    }

    @Override
    public void register() {
        super.register();

        AnvilApi.ON_ENCHANT.register(((player, map, first, second, result) -> {
            if (!map.containsKey(this)) {
                return;
            }

            if (!map.containsKey(Enchantments.INFINITY)) {
                return;
            }

            // replace mending
            map.remove(this);
            map.put(Enchantments.MENDING, 1);
        }));
    }
}
