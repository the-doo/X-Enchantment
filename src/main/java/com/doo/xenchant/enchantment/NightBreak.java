package com.doo.xenchant.enchantment;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * Night Break
 * <p>
 * from @NightBreak
 */
public class NightBreak extends BaseEnchantment {

    public static final String NAME = "night_break";
    private static final String KEY = "Count";

    private static final Text TIPS = new TranslatableText("enchantment.x_enchant.night_break.tips");

    public NightBreak() {
        super(NAME, Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 1 + (level - 1) * 50;
    }

    @Override
    public int getMaxPower(int level) {
        return level + 150;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public float getRealAdditionDamage(LivingEntity attacker, LivingEntity target, ItemStack stack, int level) {
        NbtCompound nbt = stack.getOrCreateNbt();

        long count = nbt.getLong(nbtKey(KEY));
        nbt.putLong(nbtKey(KEY), count += 1);

        // if attack 3 times, damage is level * 10% * maxHealth
        if (count % 3 == 0) {
            nbt.putLong(nbtKey(KEY), 0);
            return target.getMaxHealth() * level / 10;
        }

        return 0;
    }

    @Override
    public void register() {
        super.register();

        // tooltips
        ItemTooltipCallback.EVENT.register(((stack, context, lines) -> {
            if (level(stack) > 0) {
                lines.add(new TranslatableText(getTranslationKey()).append(": ").append(stack.getOrCreateNbt().getLong(nbtKey(KEY)) + "").formatted(Formatting.GRAY));
                lines.add(new TranslatableText(getTranslationKey()).append(" - ").append(TIPS).formatted(Formatting.GRAY));
            }
        }));
    }
}
