package com.doo.xenchantment.enchantment;

import com.doo.xenchantment.events.LootApi;
import com.doo.xenchantment.util.EnchantUtil;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Librarian extends BaseXEnchantment {

    private static final Map<Rarity, List<Enchantment>> ENCHANTMENT_MAP = Maps.newHashMap();
    private static final String TRIGGER_KEY = "value";
    private static final String UNCOMMON_VALUE_KEY = "uncommon";
    private static final String RARE_VALUE_KEY = "rare";
    private static final String VARY_RARE_VALUE_KEY = "very_rare";


    public Librarian() {
        super("librarian", Rarity.RARE, EnchantmentCategory.FISHING_ROD, EnchantUtil.ALL_HAND);

        options.addProperty(MAX_LEVEL_KEY, 3);
        options.addProperty(TRIGGER_KEY, 20);
        options.addProperty(UNCOMMON_VALUE_KEY, 30);
        options.addProperty(RARE_VALUE_KEY, 10);
        options.addProperty(VARY_RARE_VALUE_KEY, 5);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, TRIGGER_KEY);
        loadIf(json, UNCOMMON_VALUE_KEY);
        loadIf(json, RARE_VALUE_KEY);
        loadIf(json, VARY_RARE_VALUE_KEY);
    }

    @Override
    public void onServer() {
        LootApi.register(((living, stack, stacks) -> {
            if (disabled()) {
                return Collections.emptyList();
            }

            if (!(stack.getItem() instanceof FishingRodItem)) {
                return Collections.emptyList();
            }

            int level = level(stack);
            if (level < 1) {
                return Collections.emptyList();
            }

            boolean dropped = living.getRandom().nextDouble() < level * getDouble(TRIGGER_KEY) / 100;
            if (!dropped) {
                return Collections.emptyList();
            }

            // check rarity
            Rarity rarity = randRarityByLevel(living.getRandom().nextInt(100), living.getAttributeValue(Attributes.LUCK) + 1);
            List<Enchantment> list = ENCHANTMENT_MAP.get(rarity);

            if (list == null || list.isEmpty()) {
                return Collections.emptyList();
            }

            Enchantment e = list.get(living.getRandom().nextInt(list.size()));
            return Collections.singletonList(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(e, living.getRandom().nextInt(1, e.getMaxLevel()))));
        }));
    }

    /*
     * base: 5-very_rare 10-rare 30-uncommon 55-common
     */
    private Rarity randRarityByLevel(int rand, double luck) {
        if (rand < luck * getDouble(VARY_RARE_VALUE_KEY) / 100) {
            return Rarity.VERY_RARE;
        }
        if (rand < luck * getDouble(RARE_VALUE_KEY) / 100) {
            return Rarity.RARE;
        }
        if (rand < luck * getDouble(UNCOMMON_VALUE_KEY) / 100) {
            return Rarity.UNCOMMON;
        }
        return Rarity.COMMON;
    }

    @Override
    public void onServerStarted() {
        ENCHANTMENT_MAP.putAll(BuiltInRegistries.ENCHANTMENT.stream()
                .filter(Enchantment::isDiscoverable)
                .filter(e -> !e.isCurse())
                .collect(Collectors.groupingBy(Enchantment::getRarity)));
    }
}
