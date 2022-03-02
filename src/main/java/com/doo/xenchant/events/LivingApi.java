package com.doo.xenchant.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

/**
 * Living Entity Api
 */
public interface LivingApi {

    Event<ServerTailTick> SEVER_TAIL_TICK = EventFactory.createArrayBacked(ServerTailTick.class, callback -> living -> Arrays.stream(callback).forEach(c -> c.tick(living)));

    Event<OpUseTime> REDUCE_USE_TIME = EventFactory.createArrayBacked(OpUseTime.class,
            callback -> (living, stack) -> Arrays.stream(callback).mapToInt(c -> c.get(living, stack)).sum());

    Event<IgnoredApplyStatus> IGNORED_APPLY_STATUS = EventFactory.createArrayBacked(IgnoredApplyStatus.class,
            callback -> (living, effect, source) -> Arrays.stream(callback).anyMatch(c -> c.ignored(living, effect, source)));

    @FunctionalInterface
    interface ServerTailTick {
        void tick(LivingEntity living);
    }

    @FunctionalInterface
    interface OpUseTime {
        int get(LivingEntity living, ItemStack stack);
    }

    @FunctionalInterface
    interface IgnoredApplyStatus {
        boolean ignored(LivingEntity living, StatusEffect effect, Entity source);
    }
}
