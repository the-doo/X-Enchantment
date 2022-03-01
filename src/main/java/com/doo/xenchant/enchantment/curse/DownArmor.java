package com.doo.xenchant.enchantment.curse;

import com.doo.xenchant.events.EntityArmorApi;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

/**
 * DownArmor
 */
public class DownArmor extends Cursed {

    public static final String NAME = "down_armor";

    public DownArmor() {
        super(NAME, Rarity.COMMON, EnchantmentTarget.ARMOR, EquipmentSlot.values());
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public void register() {
        super.register();

        EntityArmorApi.MULTIPLIER.register(((living, base, map) -> map.containsKey(this) ? -Math.max(0, map.get(this).getRight()) / 10F : 0));
    }
}
