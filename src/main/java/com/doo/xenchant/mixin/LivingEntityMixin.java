package com.doo.xenchant.mixin;

import com.doo.xenchant.events.EntityArmorApi;
import com.doo.xenchant.events.EntityDamageApi;
import com.doo.xenchant.events.LivingApi;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected int useItemRemaining;

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void tickT(CallbackInfo ci) {
        LivingEntity living = EnchantUtil.get(this);
        if (!living.level.isClientSide) {
            LivingApi.SEVER_TAIL_TICK.invoker().tick(living);
        }
    }

    @Inject(method = "updateUsingItem", at = @At(value = "HEAD"))
    private void tickActiveItemStackH(ItemStack stack, CallbackInfo ci) {
        // need check 1 to trigger next logic
        if (useItemRemaining > 1) {
            useItemRemaining = Math.max(1, EnchantUtil.useTime(useItemRemaining, EnchantUtil.get(this), stack));
        }
    }

    @ModifyArg(method = "getArmorValue", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I"))
    private double armor(double value) {
        return EntityArmorApi.armor(value, EnchantUtil.get(this));
    }

    @ModifyVariable(method = "actuallyHurt", at = @At(value = "STORE", ordinal = 0), argsOnly = true)
    private float damageAmount(float amount, DamageSource source) {
        return EntityDamageApi.damage(amount, source, EnchantUtil.get(this));
    }

    @ModifyVariable(method = "actuallyHurt", at = @At(value = "STORE", ordinal = 1), argsOnly = true)
    private float realDamageAmount(float amount, DamageSource source) {
        return EntityDamageApi.realDamage(amount, source, EnchantUtil.get(this));
    }

    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatTracker;recordDamage(Lnet/minecraft/world/damagesource/DamageSource;FF)V"))
    private void damageCallback(DamageSource source, float amount, CallbackInfo ci) {
        Entity entity = source.getEntity();
        if (entity instanceof LivingEntity) {
            EntityDamageApi.ON_DAMAGED.invoker().call(source, (LivingEntity) entity, EnchantUtil.get(this), amount, EnchantUtil.mergeOf((LivingEntity) entity));
        }
    }

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;getEffect()Lnet/minecraft/world/effect/MobEffect;", ordinal = 0), cancellable = true)
    private void addStatusEffect(MobEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        if (LivingApi.IGNORED_APPLY_STATUS.invoker().ignored(EnchantUtil.get(this), effect.getEffect(), source)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
