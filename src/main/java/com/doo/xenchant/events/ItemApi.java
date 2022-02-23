package com.doo.xenchant.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Item is damaged
 */
public interface ItemApi {

    Event<OnEnchantment> ON_ENCHANTMENT_EVENT = EventFactory.createArrayBacked(OnEnchantment.class,
            callback -> ((stack, enchantment, level) -> Arrays.stream(callback).forEach(c -> c.call(stack, enchantment, level))));

    Event<WillDamage> WILL_DAMAGE = EventFactory.createArrayBacked(WillDamage.class,
            callback -> ((owner, stack, amount) -> Arrays.stream(callback).forEach(c -> c.call(owner, stack, amount))));

    @FunctionalInterface
    interface WillDamage {

        /**
         * Item damage will be changed
         * <p>
         * amount - value of damage
         */
        void call(@Nullable LivingEntity owner, ItemStack stack, float amount);
    }

    @FunctionalInterface
    interface OnEnchantment {

        /**
         * Item stack add enchantment
         */
        void call(ItemStack stack, Enchantment enchantment, int level);
    }
}
