package com.doo.xenchant.enchantment.trinkets;

import com.doo.xenchant.mixin.interfaces.EntityDamageApi;
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
    public void register() {
        super.register();

        EntityDamageApi.MULTIPLIER.register(((attacker, target, stack) -> level(stack) > 0 ? .2F : 0));
    }
}
