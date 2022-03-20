package com.doo.xenchant.mixin;

import com.doo.xenchant.events.ItemApi;
import com.doo.xenchant.util.EnchantUtil;
import com.google.common.collect.Multimap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"))
    private void itemUsedCallback(int amount, Random random, ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        ItemApi.WILL_DAMAGE.invoker().call(player, EnchantUtil.get(this), amount);
    }

    @ModifyVariable(method = "getAttributeModifiers", at = @At(value = "LOAD"))
    private Multimap<Attribute, AttributeModifier> addEnchantmentT(Multimap<Attribute, AttributeModifier> map, EquipmentSlot slot) {
        ItemApi.GET_MODIFIER.invoker().mod(map, EnchantUtil.get(this), slot);
        return map;
    }
}
