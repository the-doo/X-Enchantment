package com.doo.xenchant.events;

import com.doo.xenchant.enchantment.BaseEnchantment;
import com.doo.xenchant.util.EnchantUtil;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

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
    Event<OpDamage> ADD = EventFactory.createArrayBacked(OpDamage.class,
            callback -> ((source, attacker, target, map) -> {
                if (map.isEmpty()) {
                    return 0;
                }

                return (float) Arrays.stream(callback).mapToDouble(c -> c.get(source, attacker, target, map)).sum();
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
    Event<OpDamage> MULTIPLIER = EventFactory.createArrayBacked(OpDamage.class, callback -> ((source, attacker, target, map) -> {
        if (map.isEmpty()) {
            return 0;
        }

        return (float) Arrays.stream(callback).mapToDouble(c -> c.get(source, attacker, target, map)).sum();
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
    Event<OpDamage> REAL_ADD = EventFactory.createArrayBacked(OpDamage.class, callback -> ((source, attacker, target, map) -> {
        if (map.isEmpty()) {
            return 0;
        }

        return (float) Arrays.stream(callback).mapToDouble(c -> c.get(source, attacker, target, map)).sum();
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
    Event<OpDamage> REAL_MULTIPLIER = EventFactory.createArrayBacked(OpDamage.class, callback -> ((source, attacker, target, map) -> {
        if (map.isEmpty()) {
            return 0;
        }

        return (float) Arrays.stream(callback).mapToDouble(c -> c.get(source, attacker, target, map)).sum();
    }));

    /**
     * Target health is changed
     * <p>
     * amount - value of target health is changed
     */
    Event<OnDamaged> ON_DAMAGED = EventFactory.createArrayBacked(OnDamaged.class, callback -> ((source, attacker, target, amount, map) -> {
        if (map.isEmpty()) {
            return;
        }

        Arrays.stream(callback).forEach(c -> c.call(source, attacker, target, amount, map));
    }));

    @FunctionalInterface
    interface OpDamage {
        float get(DamageSource source, LivingEntity attacker, LivingEntity target, Map<BaseEnchantment, Tuple<Integer, Integer>> map);
    }

    @FunctionalInterface
    interface OnDamaged {
        void call(DamageSource source, LivingEntity attacker, LivingEntity target, float amount, Map<BaseEnchantment, Tuple<Integer, Integer>> map);
    }

    static float damage(float amount, DamageSource source, LivingEntity target) {
        Entity entity = source.getEntity();
        if (entity instanceof LivingEntity && entity != target) {
            LivingEntity attacker = (LivingEntity) entity;
            Map<BaseEnchantment, Tuple<Integer, Integer>> map = EnchantUtil.mergeOf(attacker);
            amount += EntityDamageApi.ADD.invoker().get(source, attacker, target, map);
            if (amount <= 0) {
                return 0;
            }

            amount *= (1 + EntityDamageApi.MULTIPLIER.invoker().get(source, attacker, target, map) / 100F);
            if (amount <= 0) {
                return 0;
            }
        }
        return amount;
    }

    static float realDamage(float amount, DamageSource source, LivingEntity target) {
        Entity entity = source.getEntity();
        if (entity instanceof LivingEntity && entity != target) {
            LivingEntity attacker = (LivingEntity) entity;
            Map<BaseEnchantment, Tuple<Integer, Integer>> map = EnchantUtil.mergeOf(attacker);
            amount += EntityDamageApi.REAL_ADD.invoker().get(source, attacker, target, map);
            if (amount <= 0) {
                return 0;
            }

            amount *= (1 + EntityDamageApi.REAL_MULTIPLIER.invoker().get(source, attacker, target, map) / 100F);
            if (amount <= 0) {
                return 0;
            }
        }
        return amount;
    }
}
