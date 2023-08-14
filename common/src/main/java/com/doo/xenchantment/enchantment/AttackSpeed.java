package com.doo.xenchantment.enchantment;

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

public class AttackSpeed extends BaseXEnchantment implements WithAttribute<AttackSpeed> {

    private static final java.util.UUID[] UUID = {
            java.util.UUID.fromString("93C3873C-CE04-29A3-5E54-53994013D636")
    };

    public static final String BASE_VALUE = "base_value";
    private static final List<Attribute> ATTRIBUTES = Collections.singletonList(Attributes.ATTACK_SPEED);

    public AttackSpeed() {
        super("attack_speed", Rarity.RARE, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);

        options.addProperty(MAX_LEVEL_KEY, 3);
        options.addProperty(BASE_VALUE, 30);
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
        return oneAttrModify(stackIdx(stack, slots), level, getDouble(BASE_VALUE), AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
