package com.doo.xenchant.enchantment;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

import java.util.List;

/**
 * 雷霆光环
 */
public class ThunderHalo extends HaloEnchantment {

    public static final String NAME = "thunder";

    public ThunderHalo() {
        super(NAME, false);
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public void onTarget(PlayerEntity player, Integer level, List<LivingEntity> targets) {
        targets.forEach(e -> {
            if (player.getRandom().nextInt(10) == 5) {
                e.onStruckByLightning((ServerWorld) player.world, new LightningEntity(EntityType.LIGHTNING_BOLT, e.world));
                e.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
                e.setAttacker(player);
            }
        });
    }
}
