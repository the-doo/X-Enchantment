package com.doo.xenchant.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Arrays;

/**
 * Caught Fish
 *
 * @see net.minecraft.entity.projectile.FishingBobberEntity#caughtFish
 */
public interface S2CFishCaughtCallback {

    Event<S2CFishCaughtCallback> EVENT = EventFactory.createArrayBacked(S2CFishCaughtCallback.class,
            listeners -> player -> Arrays.stream(listeners).forEach(l -> l.onCaught(player)));

    void onCaught(PlayerEntity player);
}
