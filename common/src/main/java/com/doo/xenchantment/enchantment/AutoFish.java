package com.doo.xenchantment.enchantment;

import com.doo.xenchantment.events.FishApi;
import com.doo.xenchantment.util.EnchantUtil;
import com.doo.xenchantment.util.ServersideChannelUtil;
import com.google.gson.JsonObject;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 自动钓鱼
 */
public class AutoFish extends BaseXEnchantment {

    private static final String HEALING_KEY = "healing";
    private static final String HEALING_CHANCE_KEY = "healing_rate";
    private static final String HEALING_VALUE_KEY = "healing_value";

    public AutoFish() {
        super("auto_fish", Rarity.RARE, EnchantmentCategory.FISHING_ROD, EnchantUtil.ALL_HAND);

        options.addProperty(HEALING_KEY, true);
        options.addProperty(HEALING_CHANCE_KEY, 25);
        options.addProperty(HEALING_VALUE_KEY, 1);
    }

    @Override
    protected boolean onlyOneLevel() {
        return true;
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, HEALING_KEY);
        loadIf(json, HEALING_CHANCE_KEY);
        loadIf(json, HEALING_VALUE_KEY);
    }

    @Override
    public void onServer(MinecraftServer server) {
        FishApi.register(player -> {
            if (disabled()) {
                return;
            }

            ItemStack stack = EnchantUtil.getHandStack(player, FishingRodItem.class, s -> level(s) > 0);
            if (stack == null || stack.isEmpty()) {
                return;
            }

            ServersideChannelUtil.send(player, getId(), new FriendlyByteBuf(Unpooled.buffer()));

            if (!getBoolean(HEALING_KEY)) {
                return;
            }

            // healing damage
            if (player.getRandom().nextDouble() <= getDouble(HEALING_CHANCE_KEY) / 100) {
                stack.setDamageValue(stack.getDamageValue() - getInt(HEALING_VALUE_KEY));
            }
        });
    }
}
