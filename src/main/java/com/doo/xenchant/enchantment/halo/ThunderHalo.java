package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.Enchant;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

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
    public Type getType() {
        return Type.HARMFUL;
    }

    @Override
    public void onTarget(LivingEntity entity, Integer level, List<LivingEntity> targets) {
        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(entity.getWorld());
        if (lightning == null) {
            return;
        }

        lightning.setChanneler(entity instanceof ServerPlayerEntity ? (ServerPlayerEntity) entity : null);

        targets.forEach(e -> {
            if (entity.getRandom().nextInt(100) < Enchant.option.thunderHaloStruckChance * level) {
                lightning.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(e.getBlockPos()));
                e.world.spawnEntity(lightning);
            }
        });
    }
}
