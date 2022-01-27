package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.Enchant;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

import java.util.List;

/**
 * 雷霆光环
 */
public class ThunderHalo extends LivingHalo {

    public static final String NAME = "thunder";

    public ThunderHalo() {
        super(NAME);
    }

    @Override
    public boolean isTreasure() {
        return Enchant.option.thunderHaloIsTreasure;
    }

    @Override
    protected boolean needTick() {
        return Enchant.option.thunderHalo;
    }

    @Override
    protected int second() {
        // 3s
        return 3;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void onTarget(LivingEntity entity, Integer level, List<LivingEntity> targets) {
        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, entity.world);
        lightning.setInvisible(true);

        targets.forEach(e -> {
            if (entity.getRandom().nextInt(100) < Enchant.option.thunderHaloStruckChance * level) {
                e.onStruckByLightning((ServerWorld) e.world, lightning);
                e.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
                e.setAttacker(entity);
            }
        });
    }
}
