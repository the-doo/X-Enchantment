package com.doo.xenchant.enchantment.trinkets;

import com.doo.xenchant.events.AnvilApi;
import com.doo.xenchant.events.ItemApi;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

/**
 * With Health
 */
public class WithHealth extends Trinkets {

    public static final String NAME = "with_health";

    public WithHealth() {
        super(NAME);
    }

    @Override
    public void register() {
        super.register();

        AnvilApi.ON_ENCHANT.register(((map, first, second, result) -> {
            if (!map.containsKey(this) || !(result.getItem() instanceof TrinketItem)) {
                return;
            }

            writeModifier(result);
        }));

        ItemApi.ON_ENCHANTMENT_EVENT.register(((stack, enchantment, level) -> {
            if (!(stack.getItem() instanceof TrinketItem)) {
                return;
            }

            removeModifier(stack);
        }));
    }

    @Override
    public EntityAttributeModifier getModifier() {
        return new EntityAttributeModifier("", 2, EntityAttributeModifier.Operation.ADDITION);
    }

    @Override
    public EntityAttribute getAttribute() {
        return EntityAttributes.GENERIC_MAX_HEALTH;
    }
}
