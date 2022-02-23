package com.doo.xenchant.mixin.interfaces;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

/**
 * Entity get Armor - like EntityDamageApi
 */
public interface EntityArmorApi {

    Event<EntityArmorApi.Add> ADD = EventFactory.createArrayBacked(EntityArmorApi.Add.class,
            callback -> (living, base, stack) -> (float) Arrays.stream(callback).mapToDouble(c -> c.get(living, base, stack)).sum());

    Event<EntityArmorApi.Multiplier> MULTIPLIER = EventFactory.createArrayBacked(EntityArmorApi.Multiplier.class,
            callback -> (living, base, stack) -> (float) Arrays.stream(callback).mapToDouble(c -> c.get(living, base, stack)).sum());

    @FunctionalInterface
    interface Add {
        float get(LivingEntity living, double base, ItemStack stack);
    }

    @FunctionalInterface
    interface Multiplier {
        float get(LivingEntity living, double base, ItemStack stack);
    }
}
