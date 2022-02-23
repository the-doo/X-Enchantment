package com.doo.xenchant.events;

import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Item is damaged
 */
public interface ItemApi {

    Event<GetModifier> GET_MODIFIER = EventFactory.createArrayBacked(GetModifier.class,
            callback -> ((map, stack, slot) -> Arrays.stream(callback).forEach(c -> c.mod(map, stack, slot))));

    Event<OnEnchantment> ON_ENCHANTMENT_EVENT = EventFactory.createArrayBacked(OnEnchantment.class,
            callback -> ((stack, enchantment, level) -> Arrays.stream(callback).forEach(c -> c.call(stack, enchantment, level))));


    Event<WillDamage> WILL_DAMAGE = EventFactory.createArrayBacked(WillDamage.class,
            callback -> ((owner, stack, amount) -> Arrays.stream(callback).forEach(c -> c.call(owner, stack, amount))));


    /**
     * Item stack add enchantment
     */
    @FunctionalInterface
    interface GetModifier {
        void mod(Multimap<EntityAttribute, EntityAttributeModifier> map, ItemStack stack, EquipmentSlot slot);
    }

    /**
     * Item damage will be changed
     * <p>
     * amount - value of damage
     */
    @FunctionalInterface
    interface WillDamage {
        void call(@Nullable LivingEntity owner, ItemStack stack, float amount);
    }

    /**
     * Item stack add enchantment
     */
    @FunctionalInterface
    interface OnEnchantment {
        void call(ItemStack stack, Enchantment enchantment, int level);
    }
}
