package com.doo.xenchant.enchantment.trinkets;

import com.doo.xenchant.events.ItemApi;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

/**
 * With Toughness
 */
public class WithToughness extends Trinkets {

    public static final String NAME = "with_toughness";

    public WithToughness() {
        super(NAME);
    }

    @Override
    public void register() {
        super.register();

        ItemApi.ON_ENCHANTMENT_EVENT.register(((stack, enchantment, level) -> {
            if (enchantment != this || !(stack.getItem() instanceof TrinketItem)) {
                return;
            }

            stack.addAttributeModifier(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier("", .2, EntityAttributeModifier.Operation.ADDITION), null);
        }));
    }
}
