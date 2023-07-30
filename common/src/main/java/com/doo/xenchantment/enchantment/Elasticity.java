package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.utils.ExtractAttributes;
import com.doo.xenchantment.interfaces.WithAttribute;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Collections;
import java.util.List;

/**
 * Elasticity Up
 */
public class Elasticity extends BaseXEnchantment implements WithAttribute<Elasticity> {

    private static final java.util.UUID[] UUID = {
            java.util.UUID.fromString("5ACE973F-9089-10B4-93AB-F73BF9610578"),
            java.util.UUID.fromString("5ACE973F-9089-10B4-93AB-F73BF9610577")
    };

    private static final String VALUE_KEY = "value";
    private static final List<Attribute> ATTRIBUTES = Collections.singletonList(ExtractAttributes.BOW_USING_SPEED);

    public Elasticity() {
        super("elasticity", Rarity.RARE, EnchantmentCategory.BOW, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);

        options.addProperty(MAX_LEVEL_KEY, 3);
        options.addProperty(VALUE_KEY, 10);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, VALUE_KEY);
    }

    @Override
    public List<java.util.UUID[]> getUUIDs() {
        return Collections.singletonList(UUID);
    }

    @Override
    public List<Attribute> getAttribute() {
        return ATTRIBUTES;
    }

    @Override
    public AttributeModifier getMatchModify(Attribute attribute, ItemStack stack, int level) {
        return oneAttrModify(stackIdx(stack, slots), level, getDouble(VALUE_KEY), AttributeModifier.Operation.ADDITION);
    }
}
