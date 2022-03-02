package com.doo.xenchant.enchantment;

import com.doo.xenchant.events.LootApi;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.network.ServerPlayerEntity;

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
            boolean dropped = random.nextInt(100) < 5 * level;
            if (!dropped) {
                return null;
            }

            // check rarity
            Rarity rarity = randRarityByLevel(random, level);
            return i -> {
                EnchantUtil.rand(rarity, random).ifPresent(e -> {
                    int l = random.nextInt(e.getMaxLevel());
                    trigger.dropStack(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(e, l)));

                    if (trigger instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity) trigger).addExperience(l);
                    }
                });
                return i;
            };
        }));
    }

    /*
     * base: 5-very_rare 10-rare 30-uncommon 55-common
     */
    private Rarity randRarityByLevel(Random random, int level) {
        int rand = random.nextInt(100);
        if (rand < level * 5) {
            return Rarity.VERY_RARE;
        }
        if (rand < level * 15) {
            return Rarity.RARE;
        }
        if (rand < level * 45) {
            return Rarity.UNCOMMON;
        }
        return Rarity.COMMON;
    }
}
