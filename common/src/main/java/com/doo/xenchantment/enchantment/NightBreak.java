package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.attributes.ExtractAttributes;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * Night Break
 * <p>
 * from @NightBreak
 */
public class NightBreak extends BaseXEnchantment {
    public static final java.util.UUID UUID = java.util.UUID.fromString("B2A5E445-51EA-8E7D-ACCE-2A6E0D2E5090");
    public static final String DAMAGE_KEY = "damage";
    public static final String TIP_KEY = "tip";

    private static final MutableComponent THANKS = Component.literal(" - ").append(Component.translatable("enchantment.x_enchantment.night_break.tips")).withStyle(ChatFormatting.DARK_GRAY);

    public NightBreak() {
        super("night_break", Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});

        options.addProperty(MAX_LEVEL_KEY, 5);
        options.addProperty(DAMAGE_KEY, 1.5);
        options.addProperty(TIP_KEY, true);
    }

    @Override
    public @NotNull Component getFullname(int level) {
        if (getBoolean(TIP_KEY)) {
            super.getFullname(level);
        }
        return super.getFullname(level).copy().append(THANKS);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, DAMAGE_KEY);
        loadIf(json, TIP_KEY);
    }

    @Override
    public boolean hasAttr() {
        return true;
    }

    @Override
    protected void modifiedAttrMap(ItemStack stack, int level, BiConsumer<Attribute, AttributeModifier> modifier) {
        modifier.accept(ExtractAttributes.DAMAGE_PERCENTAGE_BONUS, new AttributeModifier(UUID, name(), getDouble(DAMAGE_KEY) / 100 * level, AttributeModifier.Operation.ADDITION));
    }
}
