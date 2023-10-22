package com.doo.xenchantment.enchantment;

import com.doo.xenchantment.events.AnvilApi;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ProtectionAnvil extends BaseXEnchantment {

    private static final String PROJECTION_KEY = "projection";

    public ProtectionAnvil() {
        super("protection_anvil", Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.MAINHAND);

        options.addProperty(MAX_LEVEL_KEY, 4);
        options.addProperty(PROJECTION_KEY, 15);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, PROJECTION_KEY);
    }

    @Override
    public boolean canEnchant(ItemStack itemStack) {
        return itemStack.getItem().isEnchantable(itemStack);
    }

    @Override
    public void onServer(MinecraftServer server) {
        String cost = "RepairCost";
        AnvilApi.register((player, map, first, second, result) -> {
            if (disabled() || !map.containsKey(this) || map.get(this) < 1) {
                return;
            }

            int resultCost = result.getTag().getInt(cost);
            int old = first.getTag().getInt(cost);
            if (resultCost <= old) {
                return;
            }

            int protection = (int) ((resultCost - old) * map.get(this) * doubleV(PROJECTION_KEY) / 100);
            if (protection < 1) {
                return;
            }

            protection = resultCost - Math.min(protection, resultCost);
            if (protection < 1) {
                result.removeTagKey(cost);
            } else {
                result.getTag().putInt(cost, protection);
            }
        });
    }
}
