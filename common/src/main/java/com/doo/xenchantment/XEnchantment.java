package com.doo.xenchantment;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class XEnchantment {

    public static final String MOD_ID = "x_enchantment";
    public static final String MOD_NAME = "X-Enchantment";
    public static Supplier<Attribute> attrGetter;

    public static void init() {
    }

    public static void setAttrGetter(Supplier<Attribute> attrGetter) {
        XEnchantment.attrGetter = attrGetter;
    }

    public static void setCanBurnGetter(ICanBurn getter) {
        XEnchantment.canBurnGetter = getter;
    }

    public static Attribute rangeAttr() {
        return attrGetter.get();
    }

    public static boolean canBurn(Object o, RegistryAccess registryAccess, @Nullable Recipe<?> recipe, NonNullList<ItemStack> nonNullList, int i) {
        return canBurnGetter.canBurn(o, registryAccess, recipe, nonNullList, i);
    }


    public static ICanBurn canBurnGetter;

    public interface ICanBurn {
        boolean canBurn(Object o, RegistryAccess registryAccess, @Nullable Recipe<?> recipe, NonNullList<ItemStack> nonNullList, int i);
    }
}
