package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.utils.ExtractAttributes;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.BiConsumer;

/**
 * Elasticity Up
 */
public class Elasticity extends BaseXEnchantment {

    public static final java.util.UUID UUID = java.util.UUID.fromString("5ACE973F-9089-10B4-93AB-F73BF9610578");

    private static final String VALUE_KEY = "value";

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
    public boolean hasAttr() {
        return true;
    }

    @Override
    protected void modifiedAttrMap(ItemStack stack, int level, BiConsumer<Attribute, AttributeModifier> modifier) {
        modifier.accept(ExtractAttributes.BOW_USING_SPEED, new AttributeModifier(UUID, name(), getDouble(VALUE_KEY) / 100 * level, AttributeModifier.Operation.ADDITION));
    }
}
