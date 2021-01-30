package com.doo.xenchant.enchantment;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

/**
 * 最大生命值提升光环
 */
public class MaxHPUpHalo extends HaloEnchantment {

    public static final String NAME = "max_hp_up";

    public MaxHPUpHalo() {
        super(NAME, true);
        ATTRIBUTES.add(EntityAttributes.GENERIC_MAX_HEALTH);
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public void onTarget(PlayerEntity player, Integer level, List<LivingEntity> targets) {
        targets.forEach(e -> {
            EntityAttributeInstance attr = e.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            if (attr == null) {
                return;
            }
            addOrResetModifier(e, attr);
        });
    }
}
