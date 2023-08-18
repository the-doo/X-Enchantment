package com.doo.xenchantment.interfaces;

import net.minecraft.world.entity.LivingEntity;

public interface LivingEntityAccessor {


    static boolean canHit(Object o) {
        return ((LivingEntityAccessor) o).x_Enchantment$canHit();
    }

    boolean x_Enchantment$canHit();

}
