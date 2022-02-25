package com.doo.xenchant.enchantment;

import com.doo.xenchant.events.LootApi;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;

import java.util.Random;

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
        return 50;
    }

    @Override
    public int getMaxPower(int level) {
        return level * 50;
    }

    @Override
    public void register() {
        super.register();

        LootApi.HANDLER.register(((trigger, stack, baseConsumer, context) -> {
            int level = level(stack);
            if (level < 1) {
                return null;
            }

            if (!(stack.getItem() instanceof FishingRodItem)) {
                return null;
            }

            Entity entity = context.get(LootContextParameters.THIS_ENTITY);
            if (!(entity instanceof FishingBobberEntity)) {
                return null;
            }

            Random random = context.getRandom();
            boolean replace = random.nextInt(100) < 5 * level;
            if (!replace) {
                return null;
            }

            return i -> {
                Enchantment e = Registry.ENCHANTMENT.getRandom(random);
                if (e != null) {
                    int l = random.nextInt(e.getMaxLevel());
                    trigger.dropStack(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(e, l)));

                    if (trigger instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity) trigger).addExperience(l);
                    }
                }
                return i;
            };
        }));
    }
}
