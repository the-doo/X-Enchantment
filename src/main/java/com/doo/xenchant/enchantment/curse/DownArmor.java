package com.doo.xenchant.enchantment.curse;

import com.doo.xenchant.events.EntityArmorApi;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * DownArmor
 */
public class DownArmor extends Cursed {

    public static final String NAME = "down_armor";

    public DownArmor() {
        super(NAME, Rarity.UNCOMMON, EnchantmentCategory.ARMOR, EquipmentSlot.values());
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public void register() {
        super.register();

        EntityArmorApi.MULTIPLIER.register(((living, base, map) -> map.containsKey(this) ? -Math.max(0, map.get(this).getB()) * 10F : 0));
    }
}
