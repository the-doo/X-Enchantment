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

/**
 * Smart
 */
public class Smart extends BaseXEnchantment implements WithAttribute<Smart> {
    public static final UUID[] UUID = {
            java.util.UUID.fromString("9B48134A-D49C-7517-5FAD-20C0A47C11AC")
    };
    public static final String VALUE_KEY = "value";
    private static final List<Attribute> ATTRIBUTES = Collections.singletonList(ExtractAttributes.XP_BONUS);

    public Smart() {
        super("smart", Rarity.UNCOMMON, EnchantmentCategory.ARMOR_HEAD, EquipmentSlot.HEAD);

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
        return oneAttrModify(stackIdx(stack, slots), level, getDouble(VALUE_KEY), AttributeModifier.Operation.ADDITION);
    }
}
