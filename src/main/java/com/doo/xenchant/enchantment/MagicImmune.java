package com.doo.xenchant.enchantment;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 魔法免疫
 */
public class MagicImmune extends BaseEnchantment {

    public static final String NAME = "magic_immune";

    public MagicImmune() {
        super(NAME, Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public int getMaxLevel() {
        return 1;
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
    protected void livingTick(LivingEntity living, ItemStack stack, int level) {
        super.livingTick(living, stack, level);

        // remove all badly effect
        Set<StatusEffect> effects = living.getStatusEffects().stream()
                .map(StatusEffectInstance::getEffectType)
                .filter(effectType -> effectType.getCategory() == StatusEffectCategory.HARMFUL)
                .collect(Collectors.toSet());

        effects.forEach(living::removeStatusEffect);
    }
}
