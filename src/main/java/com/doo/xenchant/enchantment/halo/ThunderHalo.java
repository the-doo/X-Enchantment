package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.config.Option;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

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
    public boolean isTradeable() {
        return Enchant.option.thunderHaloIsTreasure;
    }

    @Override
    protected boolean ban(LivingEntity living) {
        return !Enchant.option.thunderHalo || Enchant.option.thunderHaloAllowOther == Option.AllowTarget.PLAYER && !(living instanceof ServerPlayer);
    }

    @Override
    protected float triggerTime() {
        // 5s
        return 5;
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
        boolean needHit = entity.getRandom().nextInt(100) < Enchant.option.thunderHaloStruckChance * level;
        if (!needHit) {
            return;
        }

        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(entity.level);
        if (lightning == null) {
            return;
        }

        lightning.setCause(entity instanceof ServerPlayer ? (ServerPlayer) entity : null);
        targets.stream().findFirst().ifPresent(e -> {
            lightning.moveTo(Vec3.atBottomCenterOf(e.getOnPos()));
            e.level.addFreshEntity(lightning);
        });
    }
}
