package com.doo.xenchant.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * 捕鱼回调
 * <p>
 * 当且仅当鱼被抓住时调用，caughtFish = true
 *
 * @see net.minecraft.entity.projectile.FishingBobberEntity#caughtFish
 */
@Environment(EnvType.SERVER)
public interface S2CFishCaughtCallback {

    Event<S2CFishCaughtCallback> EVENT = EventFactory.createArrayBacked(S2CFishCaughtCallback.class,
            (listeners) -> (player, item) -> {
                for (S2CFishCaughtCallback callback : listeners) {
                    callback.onCaught(player, item);
                }
            }
    );

    void onCaught(PlayerEntity player, ItemStack item);
}
