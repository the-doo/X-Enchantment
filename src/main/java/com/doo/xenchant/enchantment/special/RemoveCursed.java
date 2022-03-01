package com.doo.xenchant.enchantment.special;

import com.doo.xenchant.events.AnvilApi;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

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
    public void register() {
        super.register();

        AnvilApi.ON_ENCHANT.register(((player, map, first, second, result) -> {
            int level = level(second);
            if (level < 1) {
                return;
            }

            // remove first cursed if map has cursed
            map.keySet().stream().filter(Enchantment::isCursed).findFirst().ifPresent(map::remove);
            // remove this
            map.remove(this);
        }));
    }
}
