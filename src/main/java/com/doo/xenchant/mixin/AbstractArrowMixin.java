package com.doo.xenchant.mixin;

import com.doo.xenchant.events.PersistentApi;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    private ItemStack itemStack;

    @Inject(method = "setOwner", at = @At("TAIL"))
    private void setOwnerT(Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity) {
            itemStack = ((LivingEntity) entity).getUseItem();
            if (!itemStack.isEmpty()) {
                return;
            }

            itemStack = ((LivingEntity) entity).getMainHandItem();
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof ProjectileWeaponItem) {
                return;
            }

            itemStack = ((LivingEntity) entity).getOffhandItem();
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof ProjectileWeaponItem) {
                return;
            }

            itemStack = null;
        }
    }

    @Inject(method = "findHitEntity", at = @At("HEAD"), cancellable = true)
    private void getEntityCollisionT(Vec3 currentPosition, Vec3 nextPosition, CallbackInfoReturnable<EntityHitResult> cir) {
        AbstractArrow p = EnchantUtil.get(this);
        Entity entity = PersistentApi.ON_COLL.invoker().getEntity(p.getOwner(), itemStack, p.level, currentPosition, p.getBoundingBox());
        if (entity != null) {
            cir.setReturnValue(new EntityHitResult(entity));
            cir.cancel();
        }
    }

    @ModifyVariable(method = "shoot", at = @At(value = "HEAD"), argsOnly = true, ordinal = 0)
    private float setSpeedH(float speed, double x, double y, double z, float divergence) {
        return PersistentApi.projSpeed(speed, ((AbstractArrow) EnchantUtil.get(this)).getOwner(), itemStack);
    }
}
