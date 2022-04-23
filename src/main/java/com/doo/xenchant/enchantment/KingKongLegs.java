package com.doo.xenchant.enchantment;

import com.doo.xenchant.events.EntityDamageApi;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * King Kong Legs
 */
public class KingKongLegs extends BaseEnchantment {

    public static final String NAME = "king_kong_legs";

    public KingKongLegs() {
        super(NAME, Rarity.UNCOMMON, EnchantmentCategory.ARMOR_LEGS, new EquipmentSlot[]{EquipmentSlot.LEGS});
    }

    @Override
    public int getDamageProtection(int i, DamageSource source) {
        return !source.isBypassInvul() && source.isFall() ? Integer.MAX_VALUE : 0;
    }

    @Override
    public void register() {
        super.register();

        EntityDamageApi.MULTIPLIER.register(((source, attacker, target, map, targetMap) -> {
            if (!source.isBypassInvul() && source.isFall() && targetMap.containsKey(this)) {
                return -80;
            }

            return 0;
        }));
    }
}
