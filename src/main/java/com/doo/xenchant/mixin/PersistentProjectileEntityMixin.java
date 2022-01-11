package com.doo.xenchant.mixin;

import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends Entity {

    public PersistentProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "setOwner", at = @At("TAIL"))
    private void setOwnerT(Entity entity, CallbackInfo ci) {
        if (entity instanceof ServerPlayerEntity) {
            Box newBox = this.getBoundingBox().expand(EnchantUtil.hitRateUp(((ServerPlayerEntity) entity).getActiveItem()));
            this.setBoundingBox(newBox);
        }
    }
}
