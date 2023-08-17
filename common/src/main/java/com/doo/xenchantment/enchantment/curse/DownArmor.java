package com.doo.xenchantment.enchantment.curse;

import com.doo.xenchantment.interfaces.WithAttribute;
import com.doo.xenchantment.util.EnchantUtil;
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
 * DownArmor
 */
public class DownArmor extends Cursed implements WithAttribute<DownArmor> {
    private static final java.util.UUID[] UUIDS = {
            java.util.UUID.fromString("650203AC-2826-70BF-F18A-41C557FDE947"),
            java.util.UUID.fromString("650203AC-2826-70BF-F18A-41C557FDE946"),
            java.util.UUID.fromString("650203AC-2826-70BF-F18A-41C557FDE945"),
            java.util.UUID.fromString("650203AC-2826-70BF-F18A-41C557FDE944")
    };

    private static final String VALUE_KEY = "value";
    private static final List<Attribute> ATTRIBUTES = Collections.singletonList(Attributes.ARMOR);

    public DownArmor() {
        super("down_armor", Rarity.UNCOMMON, EnchantmentCategory.ARMOR, EnchantUtil.ALL_ARMOR);

        options.addProperty(MAX_LEVEL_KEY, 5);
        options.addProperty(VALUE_KEY, 10);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

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
