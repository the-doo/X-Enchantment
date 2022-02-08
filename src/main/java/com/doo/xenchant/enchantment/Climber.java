package com.doo.xenchant.enchantment;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;

/**
 * Climber
 */
public class Climber extends BaseEnchantment {

    public static final String NAME = "climber";

    public Climber() {
        super(NAME, Rarity.COMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public int getMinPower(int level) {
        return 50;
    }

    @Override
    public int getMaxPower(int level) {
        return 100;
    }

    @Override
    protected void livingTick(LivingEntity living, ItemStack stack, int level) {
        if (living.getY() >= 80) {
            // default level 3
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, second() * 25, 2));
        }
    }
}
