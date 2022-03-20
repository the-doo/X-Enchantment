package com.doo.xenchant.enchantment.halo;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * Height Advantage Halo
 */
public class HeightAdvantageHalo extends LivingHalo {

    public static final String NAME = "height_advantage";

    public HeightAdvantageHalo() {
        super(NAME);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public Type getType() {
        return Type.HARMFUL;
    }

    @Override
    protected float triggerTime() {
        return 3;
    }

    @Override
    public void onTarget(LivingEntity entity, Integer level, List<LivingEntity> targets) {
        targets.stream().filter(e -> entity.getEyeHeight() > e.getEyeHeight()).forEach(e -> e.hurt(DamageSource.thorns(entity), level));
    }
}
