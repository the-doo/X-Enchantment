package com.doo.xenchant.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
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

    static Consumer<ItemStack> lootConsumer(Consumer<ItemStack> lootConsumer, LootContext context) {
        // default is tool loot
        ItemStack stack = context.getParamOrNull(LootContextParams.TOOL);
        Entity entity = Optional.ofNullable(context.getParamOrNull(LootContextParams.KILLER_ENTITY))
                .orElse(context.getParamOrNull(LootContextParams.THIS_ENTITY));

        // if is attack loot, try to get on entity
        if (stack == null && entity instanceof LivingEntity) {
            stack = ((LivingEntity) entity).getMainHandItem();
        }

        // if is rod loot, try to get owner
        if (entity instanceof FishingHook) {
            entity = ((FishingHook) entity).getOwner();
        }

        if (stack == null || stack.isEmpty() || !(entity instanceof LivingEntity)) {
            return lootConsumer;
        }

        Function<ItemStack, ItemStack> handle = LootApi.HANDLER.invoker().handle((LivingEntity) entity, stack, lootConsumer, context);
        if (handle == null) {
            return lootConsumer;
        }

        return lootConsumer.andThen(handle::apply);
    }
}
