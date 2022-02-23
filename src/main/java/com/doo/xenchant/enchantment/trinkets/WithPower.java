package com.doo.xenchant.enchantment.trinkets;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.mixin.interfaces.EntityDamageApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

/**
 * With Power
 */
public class WithPower extends Trinkets {

    public static final String NAME = "with_power";

    public WithPower() {
        super(NAME);
    }

    @Override
    public void register() {
        super.register();

        EntityDamageApi.MULTIPLIER.register(((attacker, target, stack) -> level(stack) > 0 ? .2F : 0));
    }
}
