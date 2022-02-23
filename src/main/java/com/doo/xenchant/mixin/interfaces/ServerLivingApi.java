package com.doo.xenchant.mixin.interfaces;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;

import java.util.Arrays;

/**
 * Living Entity Api
 */
public interface ServerLivingApi {

    Event<TailTick> TAIL_TICK = EventFactory.createArrayBacked(TailTick.class, callback -> living -> Arrays.stream(callback).forEach(c -> c.tick(living)));

    @FunctionalInterface
    interface TailTick {
        void tick(LivingEntity living);
    }
}
