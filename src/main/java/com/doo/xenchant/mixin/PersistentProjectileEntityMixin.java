package com.doo.xenchant.mixin;

import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends Entity {

    private ServerPlayerEntity player;

    private ItemStack itemStack;

    public PersistentProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "setOwner", at = @At("TAIL"))
    private void setOwnerT(Entity entity, CallbackInfo ci) {
        if (entity instanceof ServerPlayerEntity) {
            player = (ServerPlayerEntity) entity;
            itemStack = player.getActiveItem();
        }
    }

    @Inject(method = "getEntityCollision", at = @At("HEAD"), cancellable = true)
    private void getEntityCollisionT(Vec3d currentPosition, Vec3d nextPosition, CallbackInfoReturnable<EntityHitResult> cir) {
        if (player != null && !itemStack.isEmpty()) {
            Entity entity = EnchantUtil.hitRateUp(player, itemStack, world, currentPosition, this.getBoundingBox());
            if (entity != null) {
                cir.setReturnValue(new EntityHitResult(entity));
                cir.cancel();
            }
        }
    }
}
