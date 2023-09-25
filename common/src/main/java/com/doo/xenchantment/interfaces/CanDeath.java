package com.doo.xenchantment.interfaces;

import com.doo.xenchantment.enchantment.BaseXEnchantment;
import net.minecraft.world.entity.LivingEntity;

public interface CanDeath<T extends BaseXEnchantment> {

    boolean canDeath(LivingEntity living);
}
