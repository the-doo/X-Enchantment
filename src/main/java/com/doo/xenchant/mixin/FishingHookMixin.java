package com.doo.xenchant.mixin;

import com.doo.xenchant.events.S2CFishCaughtCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {

    @Shadow
    public abstract @Nullable Player getPlayerOwner();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V", ordinal = 1), method = "catchingFish")
    private void onCaught(BlockPos pos, CallbackInfo ci) {
        Player player = getPlayerOwner();
        if (player != null) {
            S2CFishCaughtCallback.EVENT.invoker().onCaught(player);
        }
    }
}
