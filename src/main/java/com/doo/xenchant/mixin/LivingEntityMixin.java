package com.doo.xenchant.mixin;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    protected ItemStack activeItemStack;

    @Shadow
    protected int itemUseTimeLeft;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tickActiveItemStack", at = @At(value = "HEAD"), cancellable = true)
    private void tickActiveItemStackH(CallbackInfo ci) {
        if (Enchant.option.quickShoot && EnchantUtil.getServerPlayer(getUuid()) != null && activeItemStack.getItem() instanceof RangedWeaponItem && !activeItemStack.isEmpty()) {
            this.itemUseTimeLeft -= EnchantUtil.quickShooting(activeItemStack);
        }
    }

    @ModifyVariable(method = "damage", at = @At(value = "HEAD"), ordinal = 0)
    private float returnAmount(float amount, DamageSource source) {
        Entity entity;
        if (!Enchant.option.weakness || world.isClient() || (entity = source.getAttacker()) == null || !(entity instanceof ServerPlayerEntity)) {
            return amount;
        }
        return EnchantUtil.weakness((ServerPlayerEntity) entity, amount);
    }

    @Inject(at = @At("TAIL"), method = "setHealth")
    private void setHealthT(float health, CallbackInfo ci) {
        ServerPlayerEntity entity;
        if (Enchant.option.rebirth && health <= 0 && (entity = EnchantUtil.getServerPlayer(getUuid())) != null) {
            EnchantUtil.rebirth(entity);
        }
    }

    @Inject(at = @At("TAIL"), method = "applyDamage")
    private void applyDamageT(DamageSource source, float amount, CallbackInfo ci) {
        Entity entity;
        if (Enchant.option.suckBlood && amount > 0 && (entity = source.getAttacker()) != null && entity instanceof ServerPlayerEntity) {
            EnchantUtil.suckBlood((ServerPlayerEntity) entity, amount, getBoundingBox().expand(1.0D, 0.25D, 1.0D));
        }
    }
}
