package com.doo.xenchant.enchantment.special;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Health Converter
 */
public class HealthConverter extends Special {

    public static final String NAME = "health_converter";

    public HealthConverter() {
        super(NAME, Rarity.RARE, EnchantmentTarget.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return false;
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return false;
    }

    @Override
    protected float second() {
        return super.second() * 10;
    }

    @Override
    protected void livingTick(LivingEntity living, ItemStack stack, int level) {
        if (!(living instanceof ServerPlayerEntity) || !stack.isOf(Items.ENCHANTED_BOOK)) {
            return;
        }

        ((ServerPlayerEntity) living).getInventory().main.stream()
                .filter(ItemStack::isDamaged)
                .findFirst()
                .ifPresent(s -> {
                    s.setDamage(0);
                    living.damage(DamageSource.mob(living), 10);
                });
    }
}
