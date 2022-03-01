package com.doo.xenchant.events;

import com.doo.xenchant.enchantment.BaseEnchantment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

import java.util.Arrays;
import java.util.Map;

/**
 * Entity is damaged
 */
public interface EntityDamageApi {

    /**
     * Add damage amount - like EnchantmentHelper.getAttackDamage but not display on tooltips
     * <p>
     * 1 -> amount + 1
     * <p>
     * 5 -> amount + 5
     * <p>
     * -2 -> amount - 2
     */
    Event<EntityDamageApi.Add> ADD = EventFactory.createArrayBacked(EntityDamageApi.Add.class,
            callback -> ((attacker, target, map) -> {
                if (map.isEmpty()) {
                    return 0;
                }

                return (float) Arrays.stream(callback).mapToDouble(c -> c.get(attacker, target, map)).sum();
            }));

    /**
     * Multiplier of total damage amount - return value in percentage
     * <p>
     * 100 -> total * (1 + 1)
     * <p>
     * 50 -> total * (1 + 0.5)
     * <p>
     * -20 -> total * (1 - 0.2)
     */
    Event<EntityDamageApi.Multiplier> MULTIPLIER = EventFactory.createArrayBacked(EntityDamageApi.Multiplier.class,
            callback -> ((attacker, target, map) -> {
                if (map.isEmpty()) {
                    return 0;
                }

                return (float) Arrays.stream(callback).mapToDouble(c -> c.get(attacker, target, map)).sum();
            }));

    /**
     * Add real damage amount - It will add after check armor and resistance
     * <p>
     * 1 -> amount + 1
     * <p>
     * 5 -> amount + 5
     * <p>
     * -2 -> amount - 2
     */
    Event<EntityDamageApi.RealAdd> REAL_ADD = EventFactory.createArrayBacked(EntityDamageApi.RealAdd.class,
            callback -> ((attacker, target, map) -> {
                if (map.isEmpty()) {
                    return 0;
                }

                return (float) Arrays.stream(callback).mapToDouble(c -> c.get(attacker, target, map)).sum();
            }));

    /**
     * Multiplier of total real damage amount - It will add after check armor and resistance
     * <p>
     * 100 -> total * (1 + 1)
     * <p>
     * 50 -> total * (1 + 0.5)
     * <p>
     * -20 -> total * (1 - 0.2)
     */
    Event<EntityDamageApi.Multiplier> REAL_MULTIPLIER = EventFactory.createArrayBacked(EntityDamageApi.Multiplier.class,
            callback -> ((attacker, target, map) -> {
                if (map.isEmpty()) {
                    return 0;
                }

                return (float) Arrays.stream(callback).mapToDouble(c -> c.get(attacker, target, map)).sum();
            }));

    /**
     * Target health is changed
     * <p>
     * amount - value of target health is changed
     */
    Event<OnDamaged> ON_DAMAGED = EventFactory.createArrayBacked(OnDamaged.class, callback -> ((attacker, target, amount, map) -> {
        if (map.isEmpty()) {
            return;
        }

        Arrays.stream(callback).forEach(c -> c.call(attacker, target, amount, map));
    }));

    @FunctionalInterface
    interface Add {
        float get(LivingEntity attacker, LivingEntity target, Map<BaseEnchantment, Pair<Integer, Integer>> map);
    }

    @FunctionalInterface
    interface RealAdd {
        float get(LivingEntity attacker, LivingEntity target, Map<BaseEnchantment, Pair<Integer, Integer>> map);
    }

    @FunctionalInterface
    interface Multiplier {
        float get(LivingEntity attacker, LivingEntity target, Map<BaseEnchantment, Pair<Integer, Integer>> map);
    }

    @FunctionalInterface
    interface OnDamaged {
        void call(LivingEntity attacker, LivingEntity target, float amount, Map<BaseEnchantment, Pair<Integer, Integer>> map);
    }
}
