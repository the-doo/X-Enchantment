package com.doo.xenchant.enchantment.trinkets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

/**
 * With Speed
 */
public class WithSpeed extends Trinkets {

    public static final String NAME = "with_speed";

    public WithSpeed() {
        super(NAME);
    }

    @Override
    public float getMultiTotalDamage(LivingEntity attacker, LivingEntity target, ItemStack stack, int level) {
        return .2F;
    }
}
