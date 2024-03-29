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
import java.util.UUID;

public class FoodBonus extends BaseXEnchantment implements WithAttribute<FoodBonus> {
    public static final UUID[] UUID = {
            java.util.UUID.fromString("B0A4EDCD-EECC-C863-69AB-13F0AE38F961")
    };
    public static final String VALUE_KEY = "value";
    private static final List<Attribute> ATTRIBUTES = Collections.singletonList(ExtractAttributes.FOOD_BONUS);

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
    public List<java.util.UUID[]> getUUIDs() {
        return Collections.singletonList(UUID);
    }

    @Override
    public List<Attribute> getAttribute() {
        return ATTRIBUTES;
    }

    @Override
    public AttributeModifier getMatchModify(Attribute attribute, ItemStack stack, int level) {
        return oneAttrModify(stackIdx(stack, slots), level, doubleV(VALUE_KEY), AttributeModifier.Operation.ADDITION);
    }
}
