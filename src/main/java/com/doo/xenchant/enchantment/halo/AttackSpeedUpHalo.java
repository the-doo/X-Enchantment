package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.attribute.LimitTimeModifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.List;

/**
 * 攻速提升光环
 */
public class AttackSpeedUpHalo extends FriendHalo {

    public static final String NAME = "attack_speed_up";

    public AttackSpeedUpHalo() {
        super(NAME);
        ATTRIBUTES.add(EntityAttributes.GENERIC_ATTACK_SPEED);
    }

    @Override
    protected boolean needTick() {
        return Enchant.option.attackSpeedHalo;
    }

    @Override
    public void onTarget(LivingEntity entity, Integer level, List<LivingEntity> targets) {
        targets.forEach(e -> {
            EntityAttributeInstance attr = e.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED);
            if (attr == null) {
                return;
            }
            addOrResetModifier(attr, LimitTimeModifier.get(
                    getId().toString(), Enchant.option.attackSpeedHaloMultiple,
                    EntityAttributeModifier.Operation.MULTIPLY_TOTAL, e.age + 25, e));
        });
    }
}
