package com.doo.xenchant.mixin;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.network.ServerPlayerEntity;
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

    @ModifyVariable(method = "applyDamage", at = @At(value = "HEAD"), argsOnly = true)
    private float returnAmount(float amount, DamageSource source) {
        Entity entity = source.getAttacker();
        if (Enchant.option.weakness && entity instanceof LivingEntity) {
            return EnchantUtil.weakness((LivingEntity) entity, amount);
        }

        return amount;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V"), method = "applyDamage")
    private void applyDamageT(DamageSource source, float amount, CallbackInfo ci) {
        Entity entity = source.getAttacker();
        if (Enchant.option.suckBlood && entity instanceof LivingEntity) {
            EnchantUtil.suckBlood((LivingEntity) entity, amount, entity.getBoundingBox().expand(1.0D, 0.25D, 1.0D));
        }
    }

    @Inject(method = "canHaveStatusEffect", at = @At("HEAD"), cancellable = true)
    private void canHaveStatusEffectH(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity e = (LivingEntity) (Object) this;
        if (Enchant.option.magicImmune && e instanceof ServerPlayerEntity && EnchantUtil.magicImmune((ServerPlayerEntity) e, effect)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
