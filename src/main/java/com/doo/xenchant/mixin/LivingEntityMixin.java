package com.doo.xenchant.mixin;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected ItemStack activeItemStack;

    @Shadow
    protected int itemUseTimeLeft;

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void tickT(CallbackInfo ci) {
        EnchantUtil.livingTick((LivingEntity) (Object) this);
    }

    @Inject(method = "tickActiveItemStack", at = @At(value = "HEAD"))
    private void tickActiveItemStackH(CallbackInfo ci) {
        if (Enchant.option.quickShoot && activeItemStack.getItem() instanceof RangedWeaponItem) {
            this.itemUseTimeLeft -= EnchantUtil.quickShooting(activeItemStack);
        }
    }

    @ModifyVariable(method = "applyDamage", at = @At(value = "STORE", ordinal = 0), argsOnly = true)
    private float damageAmount(float amount, DamageSource source) {
        Entity entity = source.getAttacker();
        if (entity instanceof LivingEntity) {
            // is addition damage
            float addition = EnchantUtil.additionDamage((LivingEntity) entity, (LivingEntity) (Object) this);
            amount += addition;
            // is multi total damage
            float multi = EnchantUtil.multiTotalDamage((LivingEntity) entity, (LivingEntity) (Object) this);
            amount *= multi;
        }
        return amount;
    }

    @ModifyVariable(method = "applyDamage", at = @At(value = "STORE", ordinal = 1), argsOnly = true)
    private float realDamageAmount(float amount, DamageSource source) {
        Entity entity = source.getAttacker();
        if (entity instanceof LivingEntity) {
            // is addition damage
            float addition = EnchantUtil.realAdditionDamage((LivingEntity) entity, (LivingEntity) (Object) this);
            amount += addition;
            // is multi total damage
            float multi = EnchantUtil.realMultiTotalDamage((LivingEntity) entity, (LivingEntity) (Object) this);
            amount *= multi;
        }
        return amount;
    }

    @Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageTracker;onDamage(Lnet/minecraft/entity/damage/DamageSource;FF)V"))
    private void damageCallback(DamageSource source, float amount, CallbackInfo ci) {
        Entity entity = source.getAttacker();
        if (entity instanceof LivingEntity) {
            EnchantUtil.damageCallback((LivingEntity) entity, (LivingEntity) (Object) this, amount);
        }
    }

    @Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)Z", at = @At("HEAD"), cancellable = true)
    private void addStatusEffect(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity e = (LivingEntity) (Object) this;
        // true is use item and no effect
        if (Enchant.option.magicImmune && EnchantUtil.magicImmune(e, effect)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
