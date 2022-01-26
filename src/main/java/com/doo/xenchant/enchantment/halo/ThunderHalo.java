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
public class ThunderHalo extends FriendHalo {

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
    public void onTarget(LivingEntity entity, Integer level, List<LivingEntity> targets) {
        targets.forEach(e -> {
            if (entity.getRandom().nextInt(100) < Enchant.option.thunderHaloStruckChance) {
                e.onStruckByLightning((ServerWorld) entity.world, new LightningEntity(EntityType.LIGHTNING_BOLT, e.world));
                e.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
                e.setAttacker(entity);
            }
        });
    }
}
