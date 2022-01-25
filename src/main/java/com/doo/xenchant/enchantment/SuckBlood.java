package com.doo.xenchant.enchantment;

import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

/**
 * 吸血
 */
public class SuckBlood extends BaseEnchantment {

    public static final String NAME = "suck_blood";

    public SuckBlood() {
        super(NAME, Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 1 + (level - 1) * 10;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return EnchantUtil.hasAttackDamage(stack) || super.isAcceptableItem(stack);
    }
}
