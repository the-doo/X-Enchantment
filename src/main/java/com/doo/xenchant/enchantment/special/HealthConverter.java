package com.doo.xenchant.enchantment.special;

import com.doo.xenchant.events.LivingApi;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Health Converter
 */
public class HealthConverter extends Special {

    public static final String NAME = "health_converter";

    public HealthConverter() {
        super(NAME, Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public boolean canEnchant(@NotNull ItemStack itemStack) {
        return false;
    }

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment enchantment) {
        return false;
    }

    @Override
    public void register() {
        super.register();

        LivingApi.SEVER_TAIL_TICK.register(living -> {
            if (!(living instanceof ServerPlayer) || living.tickCount % (10 * SECOND) != 0) {
                return;
            }
            ServerPlayer player = (ServerPlayer) living;

            ItemStack stack = EnchantUtil.getHandStack(living, EnchantedBookItem.class, s -> level(s) > 0);
            if (stack.isEmpty()) {
                return;
            }

            // fix max damaged
            Inventory inventory = player.getInventory();
            Stream.of(inventory.items, inventory.armor, inventory.offhand)
                    .flatMap(Collection::stream)
                    .filter(ItemStack::isDamaged)
                    .max(Comparator.comparing(ItemStack::getDamageValue))
                    .ifPresent(s -> {
                        s.setDamageValue(0);
                        living.hurt(DamageSource.mobAttack(living), 10);
                    });
        });
    }
}
