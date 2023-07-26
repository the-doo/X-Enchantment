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
 * DownDamage
 */
public class DownDamage extends Cursed {
    private static final java.util.UUID UUID = java.util.UUID.fromString("E8960AAE-2680-67F5-06D8-F75FD4A061FC");
    private static final String VALUE_KEY = "value";

    public DownDamage() {
        super("down_damage", Rarity.UNCOMMON, EnchantmentCategory.WEAPON, EquipmentSlot.values());

        options.addProperty(MAX_LEVEL_KEY, 5);
        options.addProperty(VALUE_KEY, 10);
    }

    @Override
    public void loadOptions(JsonObject json) {
        loadIf(json, VALUE_KEY);
    }

    @Override
    public boolean hasAttr() {
        return true;
    }

    @Override
    protected void modifiedAttrMap(ItemStack stack, int level, BiConsumer<Attribute, AttributeModifier> modifier) {
        modifier.accept(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID, name, -getDouble(VALUE_KEY) / 100 * level, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }
}
