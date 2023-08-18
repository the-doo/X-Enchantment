package com.doo.xenchantment.mixin;

import com.doo.playerinfo.XPlayerInfo;
import com.doo.xenchantment.enchantment.WalkOn;
import com.doo.xenchantment.interfaces.LivingEntityAccessor;
import com.doo.xenchantment.util.EnchantUtil;
import com.google.common.collect.Lists;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccessor {

    @Shadow
    @Nullable
    protected Player lastHurtByPlayer;

    @Unique
    private final List<ItemStack> additionLoot = Lists.newArrayList();

    @Unique
    private int nightBreakHurtTick = -1;

    @Unique
    private boolean canStandOnFluid = false;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "dropFromLootTable", at = @At("TAIL"))
    private void additionLoot(DamageSource damageSource, boolean bl, CallbackInfo ci) {
        EnchantUtil.lootMob(damageSource, additionLoot, list -> list.forEach(this::spawnAtLocation));
    }

    @ModifyVariable(method = "getFrictionInfluencedSpeed", at = @At("HEAD"), argsOnly = true)
    private float getFrictionInfluencedSpeed(float f) {
        return canStandOnFluid ? WalkOn.getAdditionSpeed(f) : f;
    }

    @Inject(method = "canStandOnFluid", at = @At("HEAD"), cancellable = true)
    private void injectCanStandOnFluidHead(FluidState fluidState, CallbackInfoReturnable<Boolean> cir) {
        if (getPose() == Pose.STANDING && EnchantUtil.canStandOnFluid(XPlayerInfo.get(this), blockPosition(), fluidState)) {
            if (!canStandOnFluid) {
                canStandOnFluid = true;
            }
            cir.setReturnValue(true);
            cir.cancel();
        } else if (canStandOnFluid) {
            canStandOnFluid = false;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void endLivingTick(CallbackInfo ci) {
        if (!level().isClientSide()) {
            EnchantUtil.endLivingTick(XPlayerInfo.get(this));
        } else {
            canStandOnFluid = false;
        }
    }

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void onEffectAddition(MobEffectInstance mobEffectInstance, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!level().isClientSide() && !EnchantUtil.allowEffectAddition(mobEffectInstance, XPlayerInfo.get(this))) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @ModifyArg(method = "dropFromLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;JLjava/util/function/Consumer;)V"), index = 2)
    private Consumer<ItemStack> insertLootTable(Consumer<ItemStack> consumer) {
        if (lastHurtByPlayer == null) {
            return consumer;
        }

        return is -> {
            additionLoot.add(is);
            consumer.accept(is);
        };
    }

    @Override
    public boolean x_Enchantment$canHit() {
        if (nightBreakHurtTick >= tickCount) {
            return false;
        }

        nightBreakHurtTick = tickCount;
        return true;
    }
}
