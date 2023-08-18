package com.doo.xenchantment.interfaces;

public interface IntegerPropertyAccessor {

    static int max(Object prop) {
        return ((IntegerPropertyAccessor) prop).x_Enchantment$max();
    }

    int x_Enchantment$max();
}
