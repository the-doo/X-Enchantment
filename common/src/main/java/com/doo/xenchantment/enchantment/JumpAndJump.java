package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.attributes.ExtractAttributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.BiConsumer;

public class JumpAndJump extends BaseXEnchantment {

    public static final java.util.UUID UUID = java.util.UUID.fromString("1DDBA34C-4BD1-094F-9129-B64847F0251C");

    public JumpAndJump() {
        super("jump", Rarity.VERY_RARE, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});

        options.addProperty(MAX_LEVEL_KEY, 3);
    }

    @Override
    protected boolean needReconnect() {
        return true;
    }

    @Override
    public boolean hasAttr() {
        return true;
    }

    @Override
    protected void modifiedAttrMap(ItemStack stack, int level, BiConsumer<Attribute, AttributeModifier> modifier) {
        modifier.accept(ExtractAttributes.JUMP_COUNT, new AttributeModifier(UUID, name(), level, AttributeModifier.Operation.ADDITION));
    }
}
