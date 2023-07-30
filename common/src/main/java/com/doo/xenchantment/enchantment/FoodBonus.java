package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.utils.ExtractAttributes;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Smart
 */
public class FoodBonus extends BaseXEnchantment {
    public static final UUID UUID = java.util.UUID.fromString("B0A4EDCD-EECC-C863-69AB-13F0AE38F961");
    public static final String VALUE_KEY = "value";

    public FoodBonus() {
        super("food_bonus", Rarity.RARE, EnchantmentCategory.ARMOR_HEAD, EquipmentSlot.HEAD);

        options.addProperty(MAX_LEVEL_KEY, 5);
        options.addProperty(VALUE_KEY, 20);
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
        modifier.accept(ExtractAttributes.FOOD_BONUS, new AttributeModifier(UUID, name(), getDouble(VALUE_KEY) / 100 * level, AttributeModifier.Operation.ADDITION));
    }
}
