package com.doo.xenchantment.enchantment;

import com.doo.xenchantment.interfaces.OneLevelMark;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class KingKongLegs extends BaseXEnchantment implements OneLevelMark {

    public KingKongLegs() {
        super("king_kong_legs", Rarity.COMMON, EnchantmentCategory.ARMOR_LEGS, EquipmentSlot.LEGS);
    }

    @Override
    public int getDamageProtection(int i, DamageSource source) {
        return !disabled() && source.is(DamageTypes.FALL) ? 1000000000 : 0;
    }
}
