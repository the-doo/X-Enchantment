package com.doo.xenchantment.enchantment.curse;

import com.doo.xenchantment.interfaces.WithAttribute;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * DownDamage
 */
public class DownDamage extends Cursed implements WithAttribute<DownDamage> {
    private static final java.util.UUID[] UUIDS = {
            java.util.UUID.fromString("E8960AAE-2680-67F5-06D8-F75FD4A061FA"),
            java.util.UUID.fromString("E8960AAE-2680-67F5-06D8-F75FD4A061FB")
    };
    private static final String VALUE_KEY = "value";
    private static final List<Attribute> ATTRIBUTES = Collections.singletonList(Attributes.ATTACK_DAMAGE);

    public DownDamage() {
        super("down_damage", Rarity.UNCOMMON, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);

        options.addProperty(MAX_LEVEL_KEY, 5);
        options.addProperty(VALUE_KEY, 10);
    }

    @Override
    public void loadOptions(JsonObject json) {
        loadIf(json, VALUE_KEY);
    }

    @Override
    public List<UUID[]> getUUIDs() {
        return Collections.singletonList(UUIDS);
    }

    @Override
    public List<Attribute> getAttribute() {
        return ATTRIBUTES;
    }

    @Override
    public AttributeModifier getMatchModify(Attribute attribute, ItemStack stack, int level) {
        return oneAttrModify(stackIdx(stack, slots), level, -doubleV(VALUE_KEY), AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
