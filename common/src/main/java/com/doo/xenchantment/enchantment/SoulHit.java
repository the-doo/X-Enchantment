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

public class SoulHit extends BaseXEnchantment implements WithAttribute<SoulHit> {
    private static final java.util.UUID[] UUID = {
            java.util.UUID.fromString("54D161F7-2972-6124-887E-57B6B3AC8B51")
    };
    public static final String DAMAGE_KEY = "damage";
    private static final List<Attribute> ATTRIBUTES = Collections.singletonList(ExtractAttributes.DAMAGE_PERCENTAGE_BONUS);

    public SoulHit() {
        super("soul_hit", Rarity.RARE, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);

        options.addProperty(MAX_LEVEL_KEY, 3);
        options.addProperty(DAMAGE_KEY, 5);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, DAMAGE_KEY);
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
        return oneAttrModify(stackIdx(stack, slots), level, getDouble(DAMAGE_KEY), AttributeModifier.Operation.ADDITION);
    }
}
