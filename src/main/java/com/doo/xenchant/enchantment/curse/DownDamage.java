package com.doo.xenchant.enchantment.curse;

import com.doo.xenchant.events.EntityDamageApi;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * DownDamage
 */
public class DownDamage extends Cursed {

    public static final String NAME = "down_damage";

    public DownDamage() {
        super(NAME, Rarity.UNCOMMON, EnchantmentCategory.WEAPON, EquipmentSlot.values());
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public void register() {
        super.register();

        // Multiplier Total
        EntityDamageApi.MULTIPLIER.register(((source, attacker, target, map) -> map.containsKey(this) ? -Math.max(0, map.get(this).getB()) * 10F : 0));
    }
}
