package com.doo.xenchant.mixin.interfaces;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Item is damaged
 */
public interface ItemDamageApi {

    Event<WILL> WILL_DAMAGE = EventFactory.createArrayBacked(WILL.class,
            callback -> ((owner, stack, amount) -> Arrays.stream(callback).forEach(c -> c.call(owner, stack, amount))));

    @FunctionalInterface
    interface WILL {

        /**
         * Item damage will be changed
         * <p>
         * amount - value of damage
         */
        void call(@Nullable LivingEntity owner, ItemStack stack, float amount);
    }
}
