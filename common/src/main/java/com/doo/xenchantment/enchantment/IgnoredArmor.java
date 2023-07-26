package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.attributes.ExtractAttributes;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.BiConsumer;

public class IgnoredArmor extends BaseXEnchantment {

    public static final java.util.UUID UUID = java.util.UUID.fromString("AF1DF6D2-0AD8-70A3-3B68-180D887DF150");

    public static final String BASE_VALUE = "base_value";

    public IgnoredArmor() {
        super("ignored_armor", Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});

        options.addProperty(MAX_LEVEL_KEY, 3);
        options.addProperty(BASE_VALUE, 15);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, BASE_VALUE);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof TieredItem ||
                stack.getItem() instanceof ProjectileWeaponItem ||
                stack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public boolean hasAttr() {
        return true;
    }

    @Override
    protected void modifiedAttrMap(ItemStack stack, int level, BiConsumer<Attribute, AttributeModifier> modifier) {
        modifier.accept(ExtractAttributes.ARMOR_PENETRATION, new AttributeModifier(UUID, name, getDouble(BASE_VALUE) / 100 * level, AttributeModifier.Operation.ADDITION));
    }
}
