package com.doo.xenchant.enchantment.halo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

/**
 * 攻速提升光环
 */
public class AttackSpeedUpHalo extends HaloEnchantment {

    public static final String NAME = "attack_speed_up";

    public AttackSpeedUpHalo() {
        super(NAME, true);
        ATTRIBUTES.add(EntityAttributes.GENERIC_ATTACK_SPEED);
    }

    @Override
    public void onTarget(PlayerEntity player, Integer level, List<LivingEntity> targets) {
        targets.forEach(e -> {
            EntityAttributeInstance attr = e.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED);
            if (attr == null) {
                return;
            }
            addOrResetModifier(e, attr);
        });
    }
}
