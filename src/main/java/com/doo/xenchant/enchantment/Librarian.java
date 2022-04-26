package com.doo.xenchant.enchantment;

import com.doo.xenchant.events.LootApi;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Random;

/**
 * Librarian
 * <p>
 * Can change fishing loot to Enchantment book
 */
public class Librarian extends BaseEnchantment {

    public static final String NAME = "librarian";

    public Librarian() {
        super(NAME, Rarity.RARE, EnchantmentCategory.FISHING_ROD, EnchantUtil.ALL_HAND);
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

            Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
            if (!(entity instanceof FishingHook)) {
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
                EnchantUtil.rand(rarity, random).ifPresent(e ->
                        trigger.spawnAtLocation(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(e, random.nextInt(e.getMaxLevel())))));
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
