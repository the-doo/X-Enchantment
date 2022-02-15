package com.doo.xenchant.mixin;

import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

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
}
