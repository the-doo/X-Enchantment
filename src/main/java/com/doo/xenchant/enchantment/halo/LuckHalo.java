package com.doo.xenchant.enchantment.halo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

/**
 * 幸运光环
 */
public class LuckHalo extends HaloEnchantment {

    public static final String NAME = "luck";

    public LuckHalo() {
        super(NAME, true);
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public void onTarget(PlayerEntity player, Integer level, List<LivingEntity> targets) {
        targets.forEach(e -> e.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 30, 2)));
    }
}
