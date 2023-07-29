package com.doo.xenchantment.enchantment.curse;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.BiConsumer;

/**
 * DownArmor
 */
public class DownArmor extends Cursed {
    private static final java.util.UUID UUID = java.util.UUID.fromString("650203AC-2826-70BF-F18A-41C557FDE947");

    private static final String VALUE_KEY = "value";

    public DownArmor() {
        super("down_armor", Rarity.UNCOMMON, EnchantmentCategory.ARMOR, EquipmentSlot.values());

        options.addProperty(MAX_LEVEL_KEY, 5);
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
        modifier.accept(Attributes.ARMOR, new AttributeModifier(UUID, name(), -getDouble(VALUE_KEY) / 100 * level, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }
}
