package com.doo.xenchant.enchantment.curse;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

/**
 * Regicide
 */
public class Regicide extends Cursed {

    public static final String NAME = "regicide";

    public Regicide() {
        super(NAME, Rarity.COMMON, EnchantmentTarget.BREAKABLE, EquipmentSlot.values());
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
        return 4;
    }

    @Override
    public void damageCallback(LivingEntity attacker, LivingEntity target, ItemStack stack, int level, float amount) {
        float damage = level * 2;
        if (amount < damage) {
            attacker.damage(DamageSource.mob(attacker), damage);
        }
    }
}
