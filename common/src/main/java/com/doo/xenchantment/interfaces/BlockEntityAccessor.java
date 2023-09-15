package com.doo.xenchantment.interfaces;

public interface BlockEntityAccessor {

    static int timeSpeed(Object e) {
        return ((BlockEntityAccessor) e).x_Enchantment$timeSpeed();
    }

    static void timeSpeed(Object e, int incr, int max) {
        ((BlockEntityAccessor) e).x_Enchantment$timeSpeed(incr, max);
    }

    int x_Enchantment$timeSpeed();

    void x_Enchantment$timeSpeed(int incr, int max);
}
