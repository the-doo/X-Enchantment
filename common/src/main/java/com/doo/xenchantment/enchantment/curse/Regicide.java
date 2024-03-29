package com.doo.xenchantment.enchantment.curse;

import com.doo.playerinfo.core.InfoGroupItems;
import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.advancements.TrueTrigger;
import com.doo.xenchantment.events.PlayerAttackApi;
import com.doo.xenchantment.interfaces.Advable;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class Regicide extends Cursed implements Advable<Regicide> {
    private static final String VALUE_KEY = "value";

    public static final TrueTrigger DIE =
            TrueTrigger.get(new ResourceLocation(XEnchantment.MOD_ID, "trigger.regicide.die"));

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
    public void onServer(MinecraftServer server) {
        PlayerAttackApi.register((player, amount) -> {
            int level = totalLevel(player);
            if (level < 1) {
                return;
            }

            float limit = (float) (level * doubleV(VALUE_KEY));
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
        int level = totalLevel(player);

        InfoGroupItems group = InfoGroupItems.groupKey(getDescriptionId());
        group.add(getInfoKey(VALUE_KEY), level < 1 ? 0 : level * doubleV(VALUE_KEY), false);
        return group;
    }

    @Override
    public TrueTrigger getAdvTrigger() {
        return DIE;
    }
}
