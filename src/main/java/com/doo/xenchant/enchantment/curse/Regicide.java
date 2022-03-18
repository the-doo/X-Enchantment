package com.doo.xenchant.enchantment.curse;

import com.doo.xenchant.events.EntityDamageApi;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;

/**
 * Regicide
 */
public class Regicide extends Cursed {

    public static final String NAME = "regicide";

    public Regicide() {
        super(NAME, Rarity.RARE, EnchantmentTarget.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public void register() {
        super.register();

        EntityDamageApi.ON_DAMAGED.register(((source, attacker, target, amount, map) -> {
            if (attacker == target || !map.containsKey(this)) {
                return;
            }

            float limit = map.get(this).getRight() * 2;
            if (amount < limit) {
                attacker.damage(DamageSource.mob(attacker), amount);
            }
        }));
    }
}
