package com.doo.xenchant.enchantment;

import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.registry.Registry;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Librarian
 * <p>
 * Can change fishing loot to Enchantment book
 */
public class Librarian extends BaseEnchantment {

    public static final String NAME = "librarian";

    public Librarian() {
        super(NAME, Rarity.RARE, EnchantmentTarget.FISHING_ROD, EnchantUtil.ALL_HAND);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinPower(int level) {
        return 20;
    }

    @Override
    public int getMaxPower(int level) {
        return level * 50;
    }

    @Override
    public UnaryOperator<ItemStack> lootSetter(LivingEntity killer, ItemStack stack, Integer level, Consumer<ItemStack> baseConsumer, LootContext context) {
        if (!(stack.getItem() instanceof FishingRodItem)) {
            return null;
        }

        Entity entity = context.get(LootContextParameters.THIS_ENTITY);
        if (!(entity instanceof FishingBobberEntity)) {
            return null;
        }

        // reset count
        String key = "Count";
        stack.getOrCreateNbt().putInt(nbtKey(key), 0);
        return i -> {
            // get count
            int count = stack.getOrCreateNbt().getInt(nbtKey(key));
            if (count >= level) {
                return i;
            }

            Random random = context.getRandom();
            // try to replace --- 5% * level chance
            if (i.getRarity() == net.minecraft.util.Rarity.COMMON && random.nextInt(100) < 5 * level) {
                i.setCount(0);

                // Add random enchantment
                Enchantment enchantment = Registry.ENCHANTMENT.getRandom(random);
                if (enchantment == null) {
                    return i;
                }

                // increment count
                stack.getOrCreateNbt().putInt(nbtKey(key), count + 1);
                killer.dropStack(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, random.nextInt(enchantment.getMaxLevel()) + 1)));
            }
            return i;
        };
    }
}
