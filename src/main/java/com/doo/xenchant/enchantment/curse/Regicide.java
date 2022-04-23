package com.doo.xenchant.enchantment.curse;

import com.doo.xenchant.events.EntityDamageApi;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Regicide
 */
public class Regicide extends Cursed {

    public static final String NAME = "regicide";

    public Regicide() {
        super(NAME, Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public void register() {
        super.register();

        EntityDamageApi.ON_DAMAGED.register(((source, attacker, target, amount, map) -> {
            if (!(attacker instanceof LivingEntity) || !map.containsKey(this)) {
                return;
            }

            float limit = map.get(this).getB() * 2;
            if (amount < limit) {
                attacker.hurt(DamageSource.mobAttack((LivingEntity) attacker), amount);
            }
        }));
    }
}
