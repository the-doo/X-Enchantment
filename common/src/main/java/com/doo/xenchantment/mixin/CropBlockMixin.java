package com.doo.xenchantment.mixin;

import com.doo.xenchantment.interfaces.CropBlockAccessor;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin implements CropBlockAccessor {

    @Shadow
    protected abstract IntegerProperty getAgeProperty();

    @Override
    public IntegerProperty x_Enchantment$loadAgeProperty() {
        return getAgeProperty();
    }
}
