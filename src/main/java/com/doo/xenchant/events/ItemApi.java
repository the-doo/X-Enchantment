package com.doo.xenchant.events;

import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Item is damaged
 */
public interface ItemApi {

    Event<GetModifier> GET_MODIFIER = EventFactory.createArrayBacked(GetModifier.class,
            callback -> ((map, stack, slot) -> Arrays.stream(callback).forEach(c -> c.mod(map, stack, slot))));

    Event<WillDamage> WILL_DAMAGE = EventFactory.createArrayBacked(WillDamage.class,
            callback -> ((owner, stack, amount) -> Arrays.stream(callback).forEach(c -> c.call(owner, stack, amount))));


    /**
     * Item stack add enchantment
     */
    @FunctionalInterface
    interface GetModifier {
        void mod(Multimap<Attribute, AttributeModifier> map, ItemStack stack, EquipmentSlot slot);
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
}
