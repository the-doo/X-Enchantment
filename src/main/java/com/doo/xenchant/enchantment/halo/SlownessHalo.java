package com.doo.xenchant.enchantment.halo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

/**
 * 减速光环
 */
public class SlownessHalo extends HaloEnchantment {

    public static final String NAME = "slowness";

    public SlownessHalo() {
        super(NAME, false);
    }

    @Override
    public void onTarget(PlayerEntity player, Integer level, List<LivingEntity> targets) {
        targets.forEach(e -> e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 30, 1)));
    }
}
