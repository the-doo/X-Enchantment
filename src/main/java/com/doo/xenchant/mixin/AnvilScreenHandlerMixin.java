package com.doo.xenchant.mixin;

import com.doo.xenchant.events.AnvilApi;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin {

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;set(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)V"), method = "updateResult")
    public Map<Enchantment, Integer> enchantmentOnAnvil(Map<Enchantment, Integer> enchantments, ItemStack newOne) {
        AnvilScreenHandler handler = EnchantUtil.get(this);
        AnvilApi.ON_ENCHANT.invoker().handle(enchantments, handler.slots.get(0).getStack(), handler.slots.get(1).getStack(), newOne);
        return enchantments;
    }
}
