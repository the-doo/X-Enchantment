package com.doo.xenchant.events;

import com.doo.xenchant.enchantment.BaseEnchantment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

import java.util.Arrays;
import java.util.Map;

/**
 * Entity get Armor - like EntityDamageApi
 */
public interface EntityArmorApi {

    Event<OpArmor> ADD = EventFactory.createArrayBacked(OpArmor.class,
            callback -> (living, base, map) -> (float) Arrays.stream(callback).mapToDouble(c -> c.get(living, base, map)).sum());

    Event<OpArmor> MULTIPLIER = EventFactory.createArrayBacked(OpArmor.class,
            callback -> (living, base, map) -> (float) Arrays.stream(callback).mapToDouble(c -> c.get(living, base, map)).sum());

    @FunctionalInterface
    interface OpArmor {
        float get(LivingEntity living, double base, Map<BaseEnchantment, Pair<Integer, Integer>> map);
    }
}
