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
        super(NAME, Rarity.VERY_RARE, EnchantmentTarget.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinPower(int level) {
        return 150;
    }

    @Override
    public int getMaxPower(int level) {
        return level * getMinPower(level);
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    protected int second() {
        return 5;
    }

    @Override
    protected void livingTick(LivingEntity living, ItemStack stack, int level) {
        if (living instanceof PlayerEntity) {
            // add level xp
            int amount = level;
            // if epiphany - 0.0005
            if (living.getRandom().nextInt(1000) < 5) {
                amount *= 1000;
                EnchantUtil.sendMessage(living.getDisplayName(), EPIPHANY);
            }

            ((PlayerEntity) living).addExperience(amount);
        }
    }
}
