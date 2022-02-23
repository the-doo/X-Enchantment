package com.doo.xenchant.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Map;

/**
 * Entity get Armor - like EntityDamageApi
 */
public interface AnvilApi {

    Event<OnEnchanted> ON_ENCHANT = EventFactory.createArrayBacked(OnEnchanted.class,
            callback -> ((map, first, second, result) -> Arrays.stream(callback).forEach(c -> c.handle(map, first, second, result))));

    @FunctionalInterface
    interface OnEnchanted {
        void handle(Map<Enchantment, Integer> map, ItemStack first, ItemStack second, ItemStack result);
    }
}
