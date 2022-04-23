package com.doo.xenchant.enchantment.special;

import com.doo.xenchant.events.AnvilApi;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Remove Cursed
 */
public class RemoveCursed extends Special {

    public static final String NAME = "remove_cursed";

    public RemoveCursed() {
        super(NAME, Rarity.UNCOMMON, EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public void register() {
        super.register();

        AnvilApi.ON_ENCHANT.register(((player, map, first, second, result) -> {
            if (!second.is(Items.ENCHANTED_BOOK) || !map.containsKey(this)) {
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
