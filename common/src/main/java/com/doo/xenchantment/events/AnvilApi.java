package com.doo.xenchantment.events;

import com.google.common.collect.Lists;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

public interface AnvilApi {

    final class InnerE {
        static final List<AnvilApi> EVENT = Lists.newArrayList();

        private InnerE() {
        }
    }

    static void register(AnvilApi callback) {
        AnvilApi.InnerE.EVENT.add(callback);
    }

    static void call(Player player, Map<Enchantment, Integer> map, ItemStack first, ItemStack second, ItemStack result) {
        AnvilApi.InnerE.EVENT.forEach(l -> l.handle(player, map, first, second, result));
    }

    void handle(Player player, Map<Enchantment, Integer> map, ItemStack first, ItemStack second, ItemStack result);
}
