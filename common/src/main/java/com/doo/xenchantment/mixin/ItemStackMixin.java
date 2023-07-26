package com.doo.xenchantment.mixin;

import com.doo.playerinfo.XPlayerInfo;
import com.doo.xenchantment.events.ItemApi;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"))
    private void itemUsedCallback(int i, RandomSource randomSource, ServerPlayer serverPlayer, CallbackInfoReturnable<Boolean> cir) {
        ItemApi.call(serverPlayer, XPlayerInfo.get(this), i);
    }
}
