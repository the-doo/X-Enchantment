package com.doo.xenchant.mixin;

import com.doo.xenchant.events.S2CFishCaughtCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberMixin {

    @Shadow
    @Nullable
    public abstract PlayerEntity getPlayerOwner();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;set(Lnet/minecraft/entity/data/TrackedData;Ljava/lang/Object;)V", ordinal = 1), method = "tickFishingLogic")
    private void onCaught(BlockPos pos, CallbackInfo ci) {
        PlayerEntity player = getPlayerOwner();
        if (player != null) {
            S2CFishCaughtCallback.EVENT.invoker().onCaught(player);
        }
    }
}
