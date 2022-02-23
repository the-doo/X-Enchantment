package com.doo.xenchant.enchantment.special;

import com.doo.xenchant.mixin.interfaces.ServerLivingApi;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Comparator;

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
    public void register() {
        super.register();

        ServerLivingApi.TAIL_TICK.register(living -> {
            if (!(living instanceof ServerPlayerEntity) || living.age % (10 * second()) != 0) {
                return;
            }

            ItemStack stack = EnchantUtil.getHandStack(living, EnchantedBookItem.class, s -> level(s) > 0);
            if (stack.isEmpty()) {
                return;
            }

            // fix max damaged
            ((ServerPlayerEntity) living).getInventory().main.stream()
                    .filter(ItemStack::isDamaged)
                    .max(Comparator.comparing(ItemStack::getDamage))
                    .ifPresent(s -> {
                        s.setDamage(0);
                        living.damage(DamageSource.mob(living), 10);
                    });
        });
    }
}
