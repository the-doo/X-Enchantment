package com.doo.xenchantment.mixin;

import com.doo.xenchantment.events.ArrowApi;
import com.doo.xenchantment.interfaces.ArrowAccessor;
import com.doo.xenchantment.util.EnchantUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin implements ArrowAccessor {

    private LivingEntity attacker;

    private ItemStack itemStack;
    @Unique
    private float damage;
    @Unique
    private boolean canDiffusion = true;

    @ModifyArg(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 1)
    private float logDamage(float f) {
        return damage = f;
    }

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;doPostDamageEffects(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;)V"))
    private void enchantOnHitEntity(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (attacker != null) {
            ArrowApi.call(EnchantUtil.get(this), attacker, itemStack, (LivingEntity) entityHitResult.getEntity(), damage);
        }
    }


    @Inject(method = "setOwner", at = @At("TAIL"))
    private void setOwnerT(Entity entity, CallbackInfo ci) {
        if (!(entity instanceof LivingEntity e)) {
            return;
        }
        attacker = e;
        itemStack = attacker.getUseItem();
        if (!itemStack.isEmpty()) {
            return;
        }

        itemStack = attacker.getMainHandItem();
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof ProjectileWeaponItem) {
            return;
        }

        itemStack = attacker.getOffhandItem();
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof ProjectileWeaponItem) {
            return;
        }

        itemStack = null;
    }

    @Override
    public void disableDiffusion() {
        canDiffusion = false;
    }

    @Override
    public boolean canDiffusion() {
        return canDiffusion;
    }
}
