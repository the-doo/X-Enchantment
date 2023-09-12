package com.doo.xenchantment.enchantment;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class KingKongLegs extends BaseXEnchantment {

    public KingKongLegs() {
        super("king_kong_legs", Rarity.UNCOMMON, EnchantmentCategory.ARMOR_LEGS, EquipmentSlot.LEGS);
    }

    @Override
    protected boolean onlyOneLevel() {
        return true;
    }

    @Override
    public int getDamageProtection(int i, DamageSource source) {
        return !disabled() && source.is(DamageTypes.FALL) ? 1000000000 : 0;
    }
}
