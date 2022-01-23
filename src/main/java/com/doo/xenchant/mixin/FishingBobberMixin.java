package com.doo.xenchant.mixin;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.S2CFishCaughtCallback;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberMixin {

    @Shadow
    @Nullable
    public abstract PlayerEntity getPlayerOwner();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;set(Lnet/minecraft/entity/data/TrackedData;Ljava/lang/Object;)V", ordinal = 1), method = "tickFishingLogic")
    private void onCaught(BlockPos pos, CallbackInfo ci) {
        PlayerEntity player = getPlayerOwner();
        if (Enchant.option.autoFishing && player != null) {
            S2CFishCaughtCallback.EVENT.invoker().onCaught(player, EnchantUtil.getHandStack(player, FishingRodItem.class));
        }
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/criterion/FishingRodHookedCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/projectile/FishingBobberEntity;Ljava/util/Collection;)V", ordinal = 1), index = 3, method = "use")
    private Collection<ItemStack> replaceLootT(Collection<ItemStack> fishingLoots) {
        PlayerEntity player = getPlayerOwner();
        if (Enchant.option.autoFishing && player != null) {
            return EnchantUtil.replaceEnchantmentBook(fishingLoots, player.getRandom(), EnchantUtil.getHandStack(getPlayerOwner(), FishingRodItem.class));
        }
        return fishingLoots;
    }
}
