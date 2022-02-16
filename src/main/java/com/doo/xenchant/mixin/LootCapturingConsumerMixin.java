package com.doo.xenchant.mixin;

import com.blamejared.crafttweaker.api.loot.LootCapturingConsumer;
import com.doo.xenchant.config.Config;
import com.doo.xenchant.config.ILootCapturingConsumer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is dangerous, and CraftWeaker can't accept another consumer in this class
 */
@Mixin(value = LootCapturingConsumer.class, priority = Integer.MAX_VALUE)
public abstract class LootCapturingConsumerMixin implements Consumer<ItemStack>, ILootCapturingConsumer {
    private List<ItemStack> capture;
    private Consumer<ItemStack> wrapped;

    public void release(Function<List<ItemStack>, List<ItemStack>> captureModifier) {
        List<ItemStack> temp = new ArrayList<>(capture);
        capture.clear();
        captureModifier.apply(temp).forEach(this.wrapped);
        this.capture.addAll(temp);
    }

    @NotNull
    @Override
    public Consumer<ItemStack> andThen(@NotNull Consumer<? super ItemStack> after) {
        try {
            Field wrapped = LootCapturingConsumer.class.getDeclaredField("wrapped");
            wrapped.setAccessible(true);
            wrapped.set(this, ((Consumer<ItemStack>) wrapped.get(this)).andThen(after));
            wrapped.setAccessible(false);
        } catch (Exception e) {
            Config.LOGGER.error("Can't reset wrapper field", e);
            return this;
        }
        return this;
    }
}


