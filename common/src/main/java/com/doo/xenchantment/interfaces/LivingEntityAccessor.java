package com.doo.xenchantment.interfaces;

public interface LivingEntityAccessor {


    static boolean canHit(Object o) {
        return ((LivingEntityAccessor) o).x_Enchantment$canHit();
    }

    boolean x_Enchantment$canHit();

}
