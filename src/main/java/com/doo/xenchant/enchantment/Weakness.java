package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;

/**
 * 弱点攻击
 */
public class Weakness extends BaseEnchantment {

    public static final String NAME = "weakness";

    public Weakness() {
        super(new Identifier(Enchant.ID, NAME),
                Rarity.COMMON,
                EnchantmentTarget.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 5 + (level - 1) * 9;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
