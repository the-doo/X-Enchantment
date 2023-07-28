package com.doo.xenchantment.mixin;

import com.doo.xenchantment.util.EnchantUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public abstract class PowderSnowMixin {

    @Inject(method = "canEntityWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private static void canEntityWalkOnPowderSnowH(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (EnchantUtil.canEntityWalkOnPowderSnow(entity)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
