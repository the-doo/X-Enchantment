package com.doo.xenchant.enchantment;

import com.doo.xenchant.events.EntityDamageApi;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;

/**
 * King Kong Legs
 */
public class KingKongLegs extends BaseEnchantment {

    public static final String NAME = "king_kong_legs";

    public KingKongLegs() {
        super(NAME, Rarity.UNCOMMON, EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[]{EquipmentSlot.LEGS});
    }

    @Override
    public int getProtectionAmount(int level, DamageSource source) {
        return !source.isOutOfWorld() && source.isFromFalling() ? Integer.MAX_VALUE : 0;
    }

    @Override
    public void register() {
        super.register();

        // regis
        EntityDamageApi.MULTIPLIER.register(((source, attacker, target, map) -> {
            if (!source.isOutOfWorld() && source.isFromFalling() && map.containsKey(this)) {
                return -95;
            }

            return 0;
        }));
    }
}
