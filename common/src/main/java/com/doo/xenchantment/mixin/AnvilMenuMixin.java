package com.doo.xenchantment.mixin;

import com.doo.xenchantment.events.AnvilApi;
import com.doo.xenchantment.util.EnchantUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    public AnvilMenuMixin(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(menuType, i, inventory, containerLevelAccess);
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;setEnchantments(Ljava/util/Map;Lnet/minecraft/world/item/ItemStack;)V"), method = "createResult")
    public Map<Enchantment, Integer> enchantmentOnAnvil(Map<Enchantment, Integer> enchantments, ItemStack newOne) {
        AnvilMenu handler = EnchantUtil.get(this);
        AnvilApi.call(player, enchantments, handler.slots.get(0).getItem(), handler.slots.get(1).getItem(), newOne);
        return enchantments;
    }
}
