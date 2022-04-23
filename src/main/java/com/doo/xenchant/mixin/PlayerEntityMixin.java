package com.doo.xenchant.mixin;

import com.doo.xenchant.events.EntityDamageApi;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerEntityMixin {

    @ModifyVariable(method = "actuallyHurt", at = @At(value = "STORE", ordinal = 0), argsOnly = true)
    private float damageAmount(float amount, DamageSource source) {
        return EntityDamageApi.damage(amount, source, EnchantUtil.get(this), false);
    }

    @ModifyVariable(method = "actuallyHurt", at = @At(value = "STORE", ordinal = 1), argsOnly = true)
    private float realDamageAmount(float amount, DamageSource source) {
        return EntityDamageApi.damage(amount, source, EnchantUtil.get(this), true);
    }

    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatTracker;recordDamage(Lnet/minecraft/world/damagesource/DamageSource;FF)V"))
    private void damageCallback(DamageSource source, float amount, CallbackInfo ci) {
        Entity entity = source.getEntity();
        if (entity instanceof LivingEntity) {
            EntityDamageApi.ON_DAMAGED.invoker().call(source, entity, (LivingEntity) (Object) this, amount, EnchantUtil.mergeOf((LivingEntity) entity));
        }
    }
}
