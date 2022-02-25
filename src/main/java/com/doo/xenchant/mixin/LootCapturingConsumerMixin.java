package com.doo.xenchant.mixin;

import com.blamejared.crafttweaker.api.loot.LootCapturingConsumer;
import com.doo.xenchant.interfaces.ILootCapturingConsumer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is dangerous, and CraftTweaker can't accept another consumer in this class
 */
@Mixin(value = LootCapturingConsumer.class, priority = Integer.MAX_VALUE)
public abstract class LootCapturingConsumerMixin implements Consumer<ItemStack>, ILootCapturingConsumer {

    @Shadow(remap = false)
    @Final
    private List<ItemStack> capture;

    @Shadow(remap = false)
    @Final
    private Consumer<ItemStack> wrapped;

    private Consumer<ItemStack> andThen;

    public void release(Function<List<ItemStack>, List<ItemStack>> captureModifier) {
        List<ItemStack> temp = new ArrayList<>(capture);
        capture.clear();
        Consumer<ItemStack> consumer = andThen == null ? wrapped : wrapped.andThen(andThen);
        captureModifier.apply(temp).forEach(consumer);
        capture.addAll(temp);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Consumer<ItemStack> andThen(@NotNull Consumer<? super ItemStack> after) {
        andThen = andThen == null ? (Consumer<ItemStack>) after : andThen.andThen(after);
        return this;
    }
}


