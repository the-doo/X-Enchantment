package com.doo.xenchantment.forge.mixin.accessor;


import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface CanBurnAccessor {

    @Invoker("canBurn")
    boolean invokeCanBurn(RegistryAccess registryAccess, @Nullable Recipe<?> recipe, NonNullList<ItemStack> nonNullList, int i);
}
