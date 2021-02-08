package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.attribute.LimitTimeModifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
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
    protected boolean needTick() {
        return Enchant.option.maxHPHalo;
    }

    @Override
    public void onTarget(PlayerEntity player, Integer level, List<LivingEntity> targets) {
        targets.forEach(e -> {
            EntityAttributeInstance attr = e.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            if (attr == null) {
                return;
            }
            addOrResetModifier(attr,
                    LimitTimeModifier.get(getId().toString(), Enchant.option.maxHPHaloMultiple, EntityAttributeModifier.Operation.MULTIPLY_TOTAL, e.age + 25, e));
        });
    }
}
