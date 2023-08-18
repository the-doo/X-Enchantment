package com.doo.xenchantment.mixin;

import com.doo.xenchantment.interfaces.FurnaceAccessor;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin implements FurnaceAccessor {

    @Shadow
    int cookingProgress;

    @Shadow
    int cookingTotalTime;

    @Shadow
    protected NonNullList<ItemStack> items;

    @Shadow
    protected abstract boolean isLit();

    @Shadow
    @Final
    private RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> quickCheck;

    @Override
    public NonNullList<ItemStack> x_Enchantment$items() {
        return items;
    }

    @Override
    public Recipe<?> x_Enchantment$quickCheck(AbstractFurnaceBlockEntity e, ServerLevel level) {
        return quickCheck.getRecipeFor(e, level).orElse(null);
    }

    @Override
    public void x_Enchantment$incProcess(double inc) {
        if (isLit() && inc > 0) {
            cookingProgress += (int) (inc * cookingTotalTime);
            cookingProgress = cookingProgress >= cookingTotalTime ? cookingTotalTime - 1 : cookingProgress;
        }
    }
}
