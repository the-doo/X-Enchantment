package com.doo.xenchantment.mixin;

import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.interfaces.BlockEntityAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements BlockEntityAccessor {

    @Unique
    private static final String TIME_SPEED_KEY = XEnchantment.MOD_ID + "." + "time_speed";

    @Unique
    int timeSpeed = 0;

    @Inject(method = "load", at = @At("HEAD"))
    private void x_Enchantment$load(CompoundTag compoundTag, CallbackInfo ci) {
        if (compoundTag.contains(TIME_SPEED_KEY)) {
            timeSpeed = compoundTag.getInt(TIME_SPEED_KEY);
        }
    }

    @Inject(method = "saveAdditional", at = @At("HEAD"))
    private void x_Enchantment$saveAdditional(CompoundTag compoundTag, CallbackInfo ci) {
        if (timeSpeed > 0) {
            compoundTag.putInt(TIME_SPEED_KEY, timeSpeed);
        }
    }

    @Override
    public int x_Enchantment$timeSpeed() {
        return timeSpeed;
    }

    @Override
    public void x_Enchantment$timeSpeed(int incr, int max) {
        timeSpeed = Math.min(timeSpeed + incr, max);
    }
}
