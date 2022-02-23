package com.doo.xenchant.mixin.interfaces;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

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
            callback -> ((attacker, target, stack) -> (float) Arrays.stream(callback).mapToDouble(c -> c.get(attacker, target, stack)).sum()));

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
            callback -> ((attacker, target, stack) -> (float) Arrays.stream(callback).mapToDouble(c -> c.get(attacker, target, stack)).sum()));

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
            callback -> ((attacker, target, stack) -> (float) Arrays.stream(callback).mapToDouble(c -> c.get(attacker, target, stack)).sum()));

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
            callback -> ((attacker, target, stack) -> (float) Arrays.stream(callback).mapToDouble(c -> c.get(attacker, target, stack)).sum()));

    /**
     * Target health is changed
     * <p>
     * amount - value of target health is changed
     */
    Event<OnDamaged> ON_DAMAGED = EventFactory.createArrayBacked(OnDamaged.class,
            callback -> ((attacker, target, stack, amount) -> Arrays.stream(callback).forEach(c -> c.call(attacker, target, stack, amount))));

    @FunctionalInterface
    interface Add {
        float get(LivingEntity attacker, LivingEntity target, ItemStack stack);
    }

    @FunctionalInterface
    interface RealAdd {
        float get(LivingEntity attacker, LivingEntity target, ItemStack stack);
    }

    @FunctionalInterface
    interface Multiplier {
        float get(LivingEntity attacker, LivingEntity target, ItemStack stack);
    }

    @FunctionalInterface
    interface OnDamaged {
        void call(LivingEntity attacker, LivingEntity target, ItemStack stack, float amount);
    }
}
