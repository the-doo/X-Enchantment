package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.util.Identifier;

/**
 * 重生
 */
public class Rebirth extends BaseEnchantment {

    public static final String NAME = "rebirth";

    public Rebirth() {
        super(new Identifier(Enchant.ID, NAME),
                Rarity.COMMON,
                EnchantmentTarget.ARMOR,
                EnchantUtil.ALL_ARMOR);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMinPower(int level) {
        return 20;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }
}
