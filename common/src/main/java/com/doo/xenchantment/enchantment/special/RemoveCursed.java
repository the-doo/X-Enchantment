package com.doo.xenchantment.enchantment.special;

import com.doo.xenchantment.events.AnvilApi;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Remove Cursed
 */
public class RemoveCursed extends Special {

    public RemoveCursed() {
        super("remove_cursed", EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    protected boolean onlyOneLevel() {
        return true;
    }

    @Override
    public void onServer() {
        AnvilApi.register(((player, map, first, second, result) -> {
            if (disabled() || !second.is(Items.ENCHANTED_BOOK) || !map.containsKey(this)) {
                return;
            }

            int level = level(second);
            if (level < 1) {
                return;
            }

            // remove first cursed if map has cursed
            map.keySet().stream().filter(Enchantment::isCurse).findFirst().ifPresent(map::remove);
            // remove this
            map.remove(this);
        }));
    }
}
