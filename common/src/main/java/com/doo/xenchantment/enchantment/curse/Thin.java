package com.doo.xenchantment.enchantment.curse;

import com.doo.xenchantment.events.ItemApi;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Thin
 */
public class Thin extends Cursed {
    private static final String VALUE_KEY = "value";
    private static final String RATE_KEY = "rate";

    public Thin() {
        super("thin", Rarity.COMMON, EnchantmentCategory.BREAKABLE, EquipmentSlot.values());

        options.addProperty(MAX_LEVEL_KEY, 3);
        options.addProperty(RATE_KEY, 25);
        options.addProperty(VALUE_KEY, 2);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, RATE_KEY);
        loadIf(json, VALUE_KEY);
    }

    @Override
    public void onServer(MinecraftServer server) {
        ItemApi.register((owner, stack, amount) -> {
            if (owner == null) {
                return;
            }

            int level = level(stack);
            if (level < 1) {
                return;
            }

            if (owner.getRandom().nextDouble() < getDouble(RATE_KEY) / 100) {
                stack.setDamageValue((int) (stack.getDamageValue() + level * getDouble(VALUE_KEY)));
            }
        });
    }
}
