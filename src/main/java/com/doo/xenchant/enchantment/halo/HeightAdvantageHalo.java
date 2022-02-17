package com.doo.xenchant.enchantment.halo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

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
    protected float second() {
        return 3;
    }

    @Override
    public void onTarget(LivingEntity entity, Integer level, List<LivingEntity> targets) {
        targets.stream().filter(e -> entity.getHeight() > e.getHeight())
                .forEach(e -> e.damage(DamageSource.thorns(entity), level));
    }
}
