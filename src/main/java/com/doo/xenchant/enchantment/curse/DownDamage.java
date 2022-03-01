package com.doo.xenchant.enchantment.curse;

import com.doo.xenchant.events.EntityDamageApi;
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
        EntityDamageApi.MULTIPLIER.register(((attacker, target, map) -> map.containsKey(this) ? -Math.max(0, map.get(this).getRight()) / 10F : 0));
    }
}
