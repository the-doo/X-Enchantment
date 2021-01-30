package com.doo.xenchant.enchantment.halo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

/**
 * 生命恢复光环
 */
public class RegenerationHalo extends HaloEnchantment {

    public static final String NAME = "regeneration";

    public RegenerationHalo() {
        super(NAME, true);
        ATTRIBUTES.add(EntityAttributes.GENERIC_MAX_HEALTH);
    }

    @Override
    public void onTarget(PlayerEntity player, Integer level, List<LivingEntity> targets) {
        targets.forEach(e -> e.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 30, 1)));
    }
}
