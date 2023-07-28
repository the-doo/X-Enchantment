package com.doo.xenchantment.interfaces;

import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public interface CropBlockAccessor {

    static IntegerProperty get(CropBlock block) {
        return ((CropBlockAccessor) block).x_Enchantment$loadAgeProperty();
    }

    IntegerProperty x_Enchantment$loadAgeProperty();
}
