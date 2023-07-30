package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.utils.ExtractAttributes;
import com.doo.xenchantment.interfaces.WithAttribute;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Collections;
import java.util.List;

public class IgnoredArmor extends BaseXEnchantment implements WithAttribute<IgnoredArmor> {

    private static final java.util.UUID[] UUID = {
            java.util.UUID.fromString("AF1DF6D2-0AD8-70A3-3B68-180D887DF150"),
            java.util.UUID.fromString("AF1DF6D2-0AD8-70A3-3B68-180D887DF14F")
    };

    public static final String BASE_VALUE = "base_value";
    private static final List<Attribute> ATTRIBUTES = Collections.singletonList(ExtractAttributes.ARMOR_PENETRATION);

    public IgnoredArmor() {
        super("ignored_armor", Rarity.VERY_RARE, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);

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
    public List<java.util.UUID[]> getUUIDs() {
        return Collections.singletonList(UUID);
    }

    @Override
    public List<Attribute> getAttribute() {
        return ATTRIBUTES;
    }

    @Override
    public AttributeModifier getMatchModify(Attribute attribute, ItemStack stack, int level) {
        return oneAttrModify(stackIdx(stack, slots), level, getDouble(BASE_VALUE), AttributeModifier.Operation.ADDITION);
    }
}
