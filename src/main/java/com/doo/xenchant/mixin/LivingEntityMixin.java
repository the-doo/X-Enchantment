package com.doo.xenchant.mixin;

import com.doo.xenchant.events.EntityDamageApi;
import com.doo.xenchant.events.LivingApi;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
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
    protected int itemUseTimeLeft;

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void tickT(CallbackInfo ci) {
        LivingEntity living = EnchantUtil.get(this);
        if (!living.world.isClient()) {
            LivingApi.SEVER_TAIL_TICK.invoker().tick(living);
        }
    }

    @Inject(method = "tickItemStackUsage", at = @At(value = "HEAD"))
    private void tickActiveItemStackH(ItemStack stack, CallbackInfo ci) {
        // need check 1 to trigger next logic
        if (itemUseTimeLeft > 1) {
            itemUseTimeLeft = Math.max(1, EnchantUtil.useTime(itemUseTimeLeft, EnchantUtil.get(this), stack));
        }
    }

    @ModifyArg(method = "getArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;floor(D)I"))
    private double armor(double value) {
        return EnchantUtil.armor(value, EnchantUtil.get(this));
    }

    @ModifyVariable(method = "applyDamage", at = @At(value = "STORE", ordinal = 0), argsOnly = true)
    private float damageAmount(float amount, DamageSource source) {
        return EnchantUtil.damage(amount, source, EnchantUtil.get(this));
    }

    @ModifyVariable(method = "applyDamage", at = @At(value = "STORE", ordinal = 1), argsOnly = true)
    private float realDamageAmount(float amount, DamageSource source) {
        return EnchantUtil.realDamage(amount, source, EnchantUtil.get(this));
    }

    @Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageTracker;onDamage(Lnet/minecraft/entity/damage/DamageSource;FF)V"))
    private void damageCallback(DamageSource source, float amount, CallbackInfo ci) {
        Entity entity = source.getAttacker();
        if (entity instanceof LivingEntity) {
            EntityDamageApi.ON_DAMAGED.invoker().call(source, (LivingEntity) entity, EnchantUtil.get(this), amount, EnchantUtil.mergeOf((LivingEntity) entity));
        }
    }

    @Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;getEffectType()Lnet/minecraft/entity/effect/StatusEffect;", ordinal = 0), cancellable = true)
    private void addStatusEffect(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        if (LivingApi.IGNORED_APPLY_STATUS.invoker().ignored(EnchantUtil.get(this), effect.getEffectType(), source)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
