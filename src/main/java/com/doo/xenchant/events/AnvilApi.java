package com.doo.xenchant.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Map;

/**
 * Entity get Armor - like EntityDamageApi
 */
public interface AnvilApi {

    Event<OnEnchanted> ON_ENCHANT = EventFactory.createArrayBacked(OnEnchanted.class,
            callback -> ((player, map, first, second, result) -> Arrays.stream(callback).forEach(c -> c.handle(player, map, first, second, result))));

    @FunctionalInterface
    interface OnEnchanted {
        void handle(PlayerEntity player, Map<Enchantment, Integer> map, ItemStack first, ItemStack second, ItemStack result);
    }
}
