package com.doo.xenchant.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

/**
 * 捕鱼回调
 * <p>
 * 当且仅当鱼被抓住时调用，caughtFish = true
 *
 * @see net.minecraft.entity.projectile.FishingBobberEntity#caughtFish
 */
public interface S2CFishCaughtSoundCallback {

    Event<S2CFishCaughtSoundCallback> EVENT = EventFactory.createArrayBacked(S2CFishCaughtSoundCallback.class,
            (listeners) -> (world, box) -> {
                for (S2CFishCaughtSoundCallback callback : listeners) {
                    callback.isCaught(world, box);
                }
            }
    );

    void isCaught(World world, Box box);
}
