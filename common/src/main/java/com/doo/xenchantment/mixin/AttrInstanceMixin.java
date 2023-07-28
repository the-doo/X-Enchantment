package com.doo.xenchantment.mixin;

import com.doo.xenchantment.interfaces.SetDirtAccessor;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AttributeInstance.class)
public abstract class AttrInstanceMixin implements SetDirtAccessor {

    @Shadow
    protected abstract void setDirty();

    @Override
    public void setDirtAfterReload() {
        setDirty();
    }
}
