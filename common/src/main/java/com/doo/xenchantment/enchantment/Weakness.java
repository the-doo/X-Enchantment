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

/**
 * 弱点攻击
 */
public class Weakness extends BaseXEnchantment {
    public static final java.util.UUID UUID_RATE = java.util.UUID.fromString("F9155234-E79F-5068-D7F6-B76078A9A7D8");
    public static final java.util.UUID UUID = java.util.UUID.fromString("919A0DFE-BFB7-E292-A398-D0FFBDA9AC3A");
    public static final String CRIT_RATE_KEY = "crit_rate";
    public static final String CRIT_DAMAGE_KEY = "crit_damage";

    public Weakness() {
        super("weakness", Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});

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
    public boolean hasAttr() {
        return true;
    }

    @Override
    protected void modifiedAttrMap(ItemStack stack, int level, BiConsumer<Attribute, AttributeModifier> modifier) {
        modifier.accept(ExtractAttributes.CRIT_RATE, new AttributeModifier(UUID_RATE, name(), getDouble(CRIT_RATE_KEY) / 100 * level, AttributeModifier.Operation.ADDITION));
        modifier.accept(ExtractAttributes.CRIT_DAMAGE, new AttributeModifier(UUID, name(), getDouble(CRIT_DAMAGE_KEY) / 10 * level, AttributeModifier.Operation.ADDITION));
    }
}
