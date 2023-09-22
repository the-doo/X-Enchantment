package com.doo.xenchantment.interfaces;

import com.doo.xenchantment.XEnchantment;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public interface FurnaceAccessor {

    static void inc(Object e, double inc) {
        ((FurnaceAccessor) e).x_Enchantment$incProcess(inc);
    }

    static boolean canBurn(AbstractFurnaceBlockEntity e, ServerLevel level) {
        RecipeHolder<?> recipe;
        if (e.getItem(0).isEmpty() || (recipe = ((FurnaceAccessor) e).x_Enchantment$quickCheck(e, level)) == null) {
            return false;
        }

        return XEnchantment.canBurn(
                e,
                level.registryAccess(),
                recipe,
                ((FurnaceAccessor) e).x_Enchantment$items(),
                e.getMaxStackSize());
    }


    NonNullList<ItemStack> x_Enchantment$items();

    RecipeHolder<?> x_Enchantment$quickCheck(AbstractFurnaceBlockEntity e, ServerLevel level);


    void x_Enchantment$incProcess(double inc);
}
