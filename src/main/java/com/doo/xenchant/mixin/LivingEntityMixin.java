package com.doo.xenchant.mixin;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@ModifyVariable(method = "damage", at = @At(value = "HEAD"), ordinal = 0)
	private float onDamage(float amount, DamageSource source) {
		Entity entity;
		if (!Enchant.option.weakness || world.isClient() || (entity = source.getAttacker()) == null || !(entity instanceof ServerPlayerEntity)) {
			return amount;
		}
		return EnchantUtil.weakness((ServerPlayerEntity) entity, amount);
	}

	@Inject(at = @At("TAIL"), method = "applyDamage")
	private void applyDamageT(DamageSource source, float amount, CallbackInfo ci) {
		Entity entity;
		if (Enchant.option.suckBlood && amount > 0 && (entity = source.getAttacker()) != null && entity instanceof ServerPlayerEntity) {
			EnchantUtil.suckBlood((ServerPlayerEntity) entity, amount, getBoundingBox().expand(1.0D, 0.25D, 1.0D));
		}
	}
}
