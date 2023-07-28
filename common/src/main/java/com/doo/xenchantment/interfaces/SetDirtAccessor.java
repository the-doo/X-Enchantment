package com.doo.xenchantment.interfaces;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public interface SetDirtAccessor {

    static SetDirtAccessor get(AttributeInstance attr) {
        return (SetDirtAccessor) attr;
    }

    void setDirtAfterReload();
}
