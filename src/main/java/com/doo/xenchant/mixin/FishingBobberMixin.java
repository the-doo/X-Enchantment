package com.doo.xenchant.mixin;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberMixin {

    @Shadow
    private boolean caughtFish;

    @Shadow
    @Nullable
    public abstract PlayerEntity getPlayerOwner();

    @Inject(at = @At("TAIL"), method = "tick")
    private void tickT(CallbackInfo ci) {
        PlayerEntity player = this.getPlayerOwner();
        if (Enchant.option.autoFishing && caughtFish && player instanceof ServerPlayerEntity) {
            EnchantUtil.autoFish((ServerPlayerEntity) player);
        }
    }
}
