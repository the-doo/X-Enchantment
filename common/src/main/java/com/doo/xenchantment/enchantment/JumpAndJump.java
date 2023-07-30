package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.utils.ExtractAttributes;
import com.doo.xenchantment.interfaces.WithAttribute;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Collections;
import java.util.List;

public class JumpAndJump extends BaseXEnchantment implements WithAttribute<JumpAndJump> {

    private static final java.util.UUID[] UUID = {
            java.util.UUID.fromString("1DDBA34C-4BD1-094F-9129-B64847F0251C")
    };
    private static final List<Attribute> ATTRIBUTES = Collections.singletonList(ExtractAttributes.JUMP_COUNT);

    public JumpAndJump() {
        super("jump", Rarity.VERY_RARE, EnchantmentCategory.ARMOR_FEET, EquipmentSlot.FEET);

        options.addProperty(MAX_LEVEL_KEY, 3);
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
        return oneAttrModify(stackIdx(stack, slots), level, 100, AttributeModifier.Operation.ADDITION);
    }
}
