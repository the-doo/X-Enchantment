package com.doo.xenchant.mixin;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin {

    private ItemStack itemStack;

    @Inject(method = "setOwner", at = @At("TAIL"))
    private void setOwnerT(Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity) {
            itemStack = ((LivingEntity) entity).getActiveItem();
            if (!itemStack.isEmpty()) {
                return;
            }

            itemStack = ((LivingEntity) entity).getMainHandStack();
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof RangedWeaponItem) {
                return;
            }

            itemStack = ((LivingEntity) entity).getOffHandStack();
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof RangedWeaponItem) {
                return;
            }

            itemStack = null;
        }
    }

    @Inject(method = "getEntityCollision", at = @At("HEAD"), cancellable = true)
    private void getEntityCollisionT(Vec3d currentPosition, Vec3d nextPosition, CallbackInfoReturnable<EntityHitResult> cir) {
        PersistentProjectileEntity p = (PersistentProjectileEntity) (Object) this;
        if (!Enchant.option.hitRateUp && p.getOwner() != null && itemStack != null && !itemStack.isEmpty()) {
            Entity entity = EnchantUtil.hitRateUp(p.getOwner(), itemStack, p.world, currentPosition, p.getBoundingBox());
            if (entity != null) {
                cir.setReturnValue(new EntityHitResult(entity));
                cir.cancel();
            }
        }
    }

    @ModifyVariable(method = "setVelocity", at = @At(value = "HEAD"), argsOnly = true, ordinal = 0)
    private float setSpeedH(float speed, double x, double y, double z, float divergence) {
        PersistentProjectileEntity p = (PersistentProjectileEntity) (Object) this;
        if (p.getOwner() != null && itemStack != null && !itemStack.isEmpty()) {
            return speed * (1 + EnchantUtil.elasticity(itemStack));
        }
        return speed;
    }
}
