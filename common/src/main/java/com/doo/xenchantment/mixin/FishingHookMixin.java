package com.doo.xenchantment.mixin;

import com.doo.xenchantment.events.FishApi;
import com.doo.xenchantment.util.EnchantUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {

    @Shadow
    public abstract @Nullable Player getPlayerOwner();

    @Inject(at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/entity/projectile/FishingHook;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"), method = "catchingFish")
    private void onCaught(BlockPos pos, CallbackInfo ci) {
        Player player = getPlayerOwner();
        if (player != null && !player.level().isClientSide()) {
            FishApi.call((ServerPlayer) player);
        }
    }

    @ModifyVariable(method = "retrieve", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", ordinal = 0))
    private List<ItemStack> lootFish(List<ItemStack> list) {
        EnchantUtil.lootFishing(getPlayerOwner(), list, list::addAll);
        return list;
    }
}
