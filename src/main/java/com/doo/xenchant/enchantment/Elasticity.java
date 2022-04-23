package com.doo.xenchant.enchantment;

import com.doo.xenchant.events.PersistentApi;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Elasticity Up
 */
public class Elasticity extends BaseEnchantment {

    public static final String NAME = "elasticity";

    public Elasticity() {
        super(NAME, Rarity.VERY_RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void register() {
        super.register();

        PersistentApi.MULTIPLIER.register(((owner, shooter) -> Math.max(level(shooter), 0) * 100));
    }
}
