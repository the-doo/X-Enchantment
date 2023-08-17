package com.doo.xenchantment.mixin;

import com.doo.xenchantment.events.GrindstoneApi;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMixin {

    @ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 3), method = "createResult", index = 1)
    public ItemStack enchantmentOnAnvil(ItemStack stack) {
        GrindstoneApi.call(stack);
        return stack;
    }
}
