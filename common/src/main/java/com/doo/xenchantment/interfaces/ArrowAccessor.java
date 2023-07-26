package com.doo.xenchantment.interfaces;

import net.minecraft.world.entity.projectile.AbstractArrow;

public interface ArrowAccessor {

    static ArrowAccessor get(AbstractArrow arrow) {
        return (ArrowAccessor) arrow;
    }

    void disableDiffusion();

    boolean canDiffusion();
}
