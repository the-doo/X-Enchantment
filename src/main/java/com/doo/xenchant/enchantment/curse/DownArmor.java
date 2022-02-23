package com.doo.xenchant.enchantment.curse;

import com.doo.xenchant.mixin.interfaces.EntityArmorApi;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

/**
 * DownArmor
 */
public class DownArmor extends Cursed {

    public static final String NAME = "down_armor";

    public DownArmor() {
        super(NAME, Rarity.COMMON, EnchantmentTarget.ARMOR, EquipmentSlot.values());
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public void register() {
        super.register();

        EntityArmorApi.MULTIPLIER.register(((living, base, stack) -> -Math.max(0, level(stack)) / 10F));
    }
}
