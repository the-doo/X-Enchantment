package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.utils.ExtractAttributes;
import com.doo.xenchantment.interfaces.WithAttribute;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.List;

/**
 * 弱点攻击
 */
public class Weakness extends BaseXEnchantment implements WithAttribute<Weakness> {
    private static final java.util.UUID[] UUID_RATE = {
            java.util.UUID.fromString("F9155234-E79F-5068-D7F6-B76078A9A7D8")
    };
    private static final java.util.UUID[] UUID = {
            java.util.UUID.fromString("919A0DFE-BFB7-E292-A398-D0FFBDA9AC3A")
    };
    public static final String CRIT_RATE_KEY = "crit_rate";
    public static final String CRIT_DAMAGE_KEY = "crit_damage";
    private static final List<Attribute> ATTRIBUTES =
            Lists.newArrayList(ExtractAttributes.CRIT_RATE, ExtractAttributes.CRIT_DAMAGE);

    public Weakness() {
        super("weakness", Rarity.VERY_RARE, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);

        options.addProperty(MAX_LEVEL_KEY, 3);
        options.addProperty(CRIT_RATE_KEY, 10);
        options.addProperty(CRIT_DAMAGE_KEY, 10);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, CRIT_RATE_KEY);
        loadIf(json, CRIT_DAMAGE_KEY);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof TieredItem ||
                stack.getItem() instanceof ProjectileWeaponItem ||
                stack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public List<java.util.UUID[]> getUUIDs() {
        return Lists.newArrayList(UUID_RATE, UUID);
    }

    @Override
    public List<Attribute> getAttribute() {
        return ATTRIBUTES;
    }

    @Override
    public AttributeModifier getMatchModify(Attribute attribute, ItemStack stack, int level) {
        if (attribute == ATTRIBUTES.get(0)) {
            return new AttributeModifier(UUID_RATE[0], name(), getDouble(CRIT_RATE_KEY) / 100 * level, AttributeModifier.Operation.ADDITION);
        }
        return new AttributeModifier(UUID[0], name(), getDouble(CRIT_DAMAGE_KEY) / 10 * level, AttributeModifier.Operation.ADDITION);
    }
}
