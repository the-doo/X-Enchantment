package com.doo.xenchant.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Item is damaged
 */
public interface LootApi {

    Event<Handler> HANDLER = EventFactory.createArrayBacked(Handler.class, callback -> ((trigger, stack, base, context) ->
            Arrays.stream(callback).map(c -> c.handle(trigger, stack, base, context)).filter(Objects::nonNull).reduce(Function::andThen).orElse(null)));

    @FunctionalInterface
    interface Handler {
        @Nullable
        Function<ItemStack, ItemStack> handle(LivingEntity trigger, ItemStack stack, Consumer<ItemStack> baseConsumer, LootContext context);
    }
}
