package com.doo.xenchantment.mixin;

import com.doo.playerinfo.XPlayerInfo;
import com.doo.xenchantment.events.PlayerAttackApi;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract boolean hurt(DamageSource damageSource, float f);

    @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), index = 1)
    private float damageCallback(DamageSource source, float amount) {
        if (level().isClientSide()) {
            return amount;
        }

        PlayerAttackApi.call(XPlayerInfo.get(this), amount);
        return amount;
    }
}
