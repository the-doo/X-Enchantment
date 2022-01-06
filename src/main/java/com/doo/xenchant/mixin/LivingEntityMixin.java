package com.doo.xenchant.mixin;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    protected ItemStack activeItemStack;

    @Shadow
    protected int itemUseTimeLeft;

    private int haloTick;

    @Shadow public abstract Iterable<ItemStack> getArmorItems();

    @Shadow public abstract AttributeContainer getAttributes();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void tickT(CallbackInfo ci) {
        EnchantUtil.removedDirtyHalo(getAttributes());
        if (Enchant.option.halo && age - haloTick >= Enchant.option.haloInterval) {
            haloTick = age;
            EnchantUtil.halo(uuid, getArmorItems());
        }
    }

    @Inject(method = "tickActiveItemStack", at = @At(value = "HEAD"), cancellable = true)
    private void tickActiveItemStackH(CallbackInfo ci) {
        if (Enchant.option.quickShoot && EnchantUtil.getServerPlayer(uuid) != null
                && activeItemStack.getItem() instanceof RangedWeaponItem && !activeItemStack.isEmpty()) {
            this.itemUseTimeLeft -= EnchantUtil.quickShooting(activeItemStack);
        }
    }

    @ModifyVariable(method = "damage", at = @At(value = "HEAD"), ordinal = 0)
    private float returnAmount(float amount, DamageSource source) {
        Entity entity;
        if (!Enchant.option.weakness || world.isClient() || (entity = source.getAttacker()) == null
                || !(entity instanceof ServerPlayerEntity)) {
            return amount;
        }
        return EnchantUtil.weakness((ServerPlayerEntity) entity, amount);
    }

    @Inject(at = @At("TAIL"), method = "setHealth")
    private void setHealthT(float health, CallbackInfo ci) {
        ServerPlayerEntity entity;
        if (Enchant.option.reborn && health <= 0 && (entity = EnchantUtil.getServerPlayer(uuid)) != null) {
            EnchantUtil.reborn(entity);
        }
    }

    @Inject(at = @At("TAIL"), method = "applyDamage")
    private void applyDamageT(DamageSource source, float amount, CallbackInfo ci) {
        Entity entity;
        if (Enchant.option.suckBlood && amount > 0 && (entity = source.getAttacker()) != null && entity instanceof ServerPlayerEntity) {
            EnchantUtil.suckBlood((ServerPlayerEntity) entity, amount, getBoundingBox().expand(1.0D, 0.25D, 1.0D));
        }
    }

    @Inject(method = "canHaveStatusEffect", at = @At("HEAD"), cancellable = true)
    private void canHaveStatusEffectH(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        if (Enchant.option.magicImmune && EnchantUtil.magicImmune(uuid, effect)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
