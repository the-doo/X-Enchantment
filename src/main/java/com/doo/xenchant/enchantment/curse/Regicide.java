package com.doo.xenchant.enchantment.curse;

import com.doo.xenchant.mixin.interfaces.EntityDamageApi;
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
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public void register() {
        super.register();

        EntityDamageApi.ON_DAMAGED.register(((attacker, target, stack, amount) -> {
            if (attacker == target) {
                return;
            }

            float limit = level(stack) * 2;
            if (amount < limit) {
                attacker.damage(DamageSource.mob(attacker), limit);
            }
        }));
    }
}
