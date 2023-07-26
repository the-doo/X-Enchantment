package com.doo.xenchantment.enchantment.curse;

import com.doo.playerinfo.core.InfoGroupItems;
import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.enchantment.advancements.TrueTrigger;
import com.doo.xenchantment.events.PlayerAttackApi;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Regicide
 */
public class Regicide extends Cursed {
    private static final String VALUE_KEY = "value";

    public static final TrueTrigger DIE =
            TrueTrigger.get(new ResourceLocation(XEnchantment.MOD_ID + ":trigger.regicide.die"));

    public Regicide() {
        super("regicide", Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.values());

        options.addProperty(MAX_LEVEL_KEY, 4);
        options.addProperty(VALUE_KEY, 2);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, VALUE_KEY);
    }

    @Override
    public void onServer() {
        PlayerAttackApi.register((player, amount) -> {
            int level = level(player.getMainHandItem());
            if (level < 1) {
                return;
            }

            float limit = (float) (level * getDouble(VALUE_KEY));
            if (amount <= limit) {
                player.hurt(player.damageSources().playerAttack(player), amount);

                if (player.isDeadOrDying()) {
                    DIE.trigger(player);
                }
            }
        });
    }

    @Override
    public InfoGroupItems collectPlayerInfo(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        int level = level(stack);

        InfoGroupItems group = InfoGroupItems.groupKey(getDescriptionId());
        group.add(getInfoKey(VALUE_KEY), level < 1 ? 0 : level * getDouble(VALUE_KEY), false);
        return group;
    }

    @Override
    public boolean hasAdv() {
        return true;
    }

    @Override
    public TrueTrigger getAdvTrigger() {
        return DIE;
    }
}
