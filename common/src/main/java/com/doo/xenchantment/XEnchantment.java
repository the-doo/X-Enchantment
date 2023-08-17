package com.doo.xenchantment;

import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.function.Supplier;

public class XEnchantment {

    public static final String MOD_ID = "x_enchantment";
    public static final String MOD_NAME = "X-Enchantment";
    public static Supplier<Attribute> attrGetter;

    public static void init(Supplier<Attribute> attrGetter) {
        XEnchantment.attrGetter = attrGetter;
    }

    public static Attribute rangeAttr() {
        return attrGetter.get();
    }
}
