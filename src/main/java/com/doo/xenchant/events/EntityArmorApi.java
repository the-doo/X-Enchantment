package com.doo.xenchant.events;

import com.doo.xenchant.enchantment.BaseEnchantment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;

import java.util.Arrays;
import java.util.Map;

/**
 * Entity get Armor - like EntityDamageApi
 */
public interface EntityArmorApi {

    Event<EntityArmorApi.Add> ADD = EventFactory.createArrayBacked(EntityArmorApi.Add.class,
            callback -> (living, base, map) -> (float) Arrays.stream(callback).mapToDouble(c -> c.get(living, base, map)).sum());

    Event<EntityArmorApi.Multiplier> MULTIPLIER = EventFactory.createArrayBacked(EntityArmorApi.Multiplier.class,
            callback -> (living, base, map) -> (float) Arrays.stream(callback).mapToDouble(c -> c.get(living, base, map)).sum());

    @FunctionalInterface
    interface Add {
        float get(LivingEntity living, double base, Map<BaseEnchantment, Integer> map);
    }

    @FunctionalInterface
    interface Multiplier {
        float get(LivingEntity living, double base, Map<BaseEnchantment, Integer> map);
    }
}
