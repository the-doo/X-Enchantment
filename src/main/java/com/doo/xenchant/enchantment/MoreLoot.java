package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Wearable;
import net.minecraft.util.Identifier;

/**
 * 更多战利品
 */
public class MoreLoot extends BaseEnchantment {

    public static final String NAME = "more_loot";

    public MoreLoot() {
        super(new Identifier(Enchant.ID, NAME), Rarity.RARE, EnchantmentTarget.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
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
        return 5;
    }

    @Override
    public void register() {
        super.register();
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return !(stack.getItem() instanceof Wearable);
    }
}
