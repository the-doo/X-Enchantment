package com.doo.xenchant.mixin;

import com.doo.xenchant.events.GrindApi;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin {

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;setEnchantments(Ljava/util/Map;Lnet/minecraft/world/item/ItemStack;)V"), method = "removeNonCurses")
    public Map<Enchantment, Integer> enchantmentOnGrinds(Map<Enchantment, Integer> enchantments, ItemStack stack) {
        GrindstoneMenu handler = EnchantUtil.get(this);
        GrindApi.ON_ENCHANT.invoker().handle(enchantments, handler.slots.get(0).getItem(), handler.slots.get(1).getItem(), stack);
        return enchantments;
    }
}
