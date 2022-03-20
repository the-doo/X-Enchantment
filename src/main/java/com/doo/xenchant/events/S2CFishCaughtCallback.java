package com.doo.xenchant.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;

/**
 * Caught Fish
 * <p>
 * can see: net.minecraft.entity.projectile.FishingHook#caughtFish
 */
public interface S2CFishCaughtCallback {

    Event<S2CFishCaughtCallback> EVENT = EventFactory.createArrayBacked(S2CFishCaughtCallback.class,
            listeners -> player -> Arrays.stream(listeners).forEach(l -> l.onCaught(player)));

    void onCaught(Player player);
}
