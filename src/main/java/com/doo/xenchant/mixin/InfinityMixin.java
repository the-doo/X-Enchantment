package com.doo.xenchant.mixin;

import com.doo.xenchant.Enchant;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.InfinityEnchantment;
import net.minecraft.enchantment.MendingEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InfinityEnchantment.class)
public abstract class InfinityMixin {

    @Inject(at = @At("HEAD"), method = "canAccept", cancellable = true)
    public void canAcceptH(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
        if (Enchant.option.infinityAcceptMending && other instanceof MendingEnchantment) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
