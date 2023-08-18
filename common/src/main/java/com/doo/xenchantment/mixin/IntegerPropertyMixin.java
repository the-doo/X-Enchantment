package com.doo.xenchantment.mixin;

import com.doo.xenchantment.interfaces.IntegerPropertyAccessor;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IntegerProperty.class)
public abstract class IntegerPropertyMixin implements IntegerPropertyAccessor {

    @Shadow
    @Final
    private int max;

    public int x_Enchantment$max() {
        return max;
    }
}
