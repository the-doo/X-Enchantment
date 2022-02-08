package com.doo.xenchant.enchantment;

import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * Smart
 */
public class Smart extends BaseEnchantment {

    public static final String NAME = "smart";

    public static final MutableText EPIPHANY = new TranslatableText("enchantment.x_enchant.chat.smart")
            .setStyle(Style.EMPTY.withColor(Formatting.GOLD));

    public Smart() {
        super(NAME, Rarity.RARE, EnchantmentTarget.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinPower(int level) {
        return 50;
    }

    @Override
    public int getMaxPower(int level) {
        return level * getMinPower(level);
    }

    @Override
    protected int second() {
        return 5;
    }

    @Override
    protected void livingTick(LivingEntity living, ItemStack stack, int level) {
        if (living instanceof PlayerEntity) {
            // add xp: Max of (level, level * next * 0.01)
            int amount = Math.max(100, ((PlayerEntity) living).getNextLevelExperience()) / 100 * level;
            // if epiphany
            if (living.getRandom().nextFloat() <= 0.001) {
                amount *= 100;
                EnchantUtil.sendMessage(living.getDisplayName(), EPIPHANY);
            }

            ((PlayerEntity) living).addExperience(amount);
        }
    }
}
