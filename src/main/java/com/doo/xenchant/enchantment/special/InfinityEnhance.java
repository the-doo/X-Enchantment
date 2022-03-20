package com.doo.xenchant.enchantment.special;

import com.doo.xenchant.events.AnvilApi;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

/**
 * Infinity Enhance
 */
public class InfinityEnhance extends Special {

    public static final String NAME = "infinity_enhance";

    public InfinityEnhance() {
        super(NAME, Rarity.UNCOMMON, EnchantmentCategory.BOW, EquipmentSlot.values());
    }

    @Override
    public boolean canEnchant(@NotNull ItemStack stack) {
        return EnchantmentHelper.getEnchantments(stack).containsKey(Enchantments.INFINITY_ARROWS);
    }

    @Override
    public void register() {
        super.register();

        AnvilApi.ON_ENCHANT.register(((player, map, first, second, result) -> {
            if (second.is(Items.ENCHANTED_BOOK) && map.containsKey(this) && map.containsKey(Enchantments.INFINITY_ARROWS)) {
                // replace mending
                map.remove(this);
                map.put(Enchantments.MENDING, 1);
            }
        }));
    }
}
