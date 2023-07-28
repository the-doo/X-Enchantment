package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.attributes.ExtractAttributes;
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
public class Smart extends BaseXEnchantment {
    public static final UUID UUID = java.util.UUID.fromString("9B48134A-D49C-7517-5FAD-20C0A47C11AC");
    public static final String VALUE_KEY = "value";

    public Smart() {
        super("smart", Rarity.UNCOMMON, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});

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
        modifier.accept(ExtractAttributes.XP_BONUS, new AttributeModifier(UUID, name, getDouble(VALUE_KEY) / 100 * level, AttributeModifier.Operation.ADDITION));
    }
}
