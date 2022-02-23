package com.doo.xenchant.enchantment.curse;

import com.doo.xenchant.mixin.interfaces.EntityDamageApi;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

/**
 * DownDamage
 */
public class DownDamage extends Cursed {

    public static final String NAME = "down_damage";

    public DownDamage() {
        super(NAME, Rarity.COMMON, EnchantmentTarget.WEAPON, EquipmentSlot.values());
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public void register() {
        super.register();

        // Multiplier Total
        EntityDamageApi.MULTIPLIER.register(((attacker, target, stack) -> -Math.max(0, level(stack)) / 10F));
    }
}
