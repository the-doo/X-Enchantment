package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.core.InfoGroupItems;
import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.advancements.TrueTrigger;
import com.doo.xenchantment.events.LootApi;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class MoreLoot extends BaseXEnchantment {

    public static final TrueTrigger SUPER_LOOT_TRIGGER =
            TrueTrigger.get(new ResourceLocation(XEnchantment.MOD_ID + ":trigger.more_loot.super_loot"));

    public static final String LOOT_RATE = "loot_rate";
    public static final String SUPER_LOOT_RATE = "super_loot_rate";
    public static final String SUPER_LOOT_VALUE = "super_loot_value";

    public MoreLoot() {
        super("more_loot", Rarity.RARE, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);

        options.addProperty(MAX_LEVEL_KEY, 5);
        options.addProperty(LOOT_RATE, 40);
        options.addProperty(SUPER_LOOT_RATE, 0.5);
        options.addProperty(SUPER_LOOT_VALUE, 10);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, LOOT_RATE);
        loadIf(json, SUPER_LOOT_RATE);
        loadIf(json, SUPER_LOOT_VALUE);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return !(stack.getItem() instanceof Equipable);
    }

    @Override
    public void onServer(MinecraftServer server) {
        LootApi.register((living, stack, stacks) -> {
            if (disabled()) {
                return Collections.emptyList();
            }

            long count = stacks.stream().filter(ItemStack::isStackable).count();
            if (count < 1) {
                return Collections.emptyList();
            }

            int level = level(stack);
            if (level < 1) {
                return Collections.emptyList();
            }

            MutableBoolean isSuper = new MutableBoolean();
            int rand = baseIfSuper(level, living.getRandom().nextDouble(), isSuper::setValue);
            if (rand < 1) {
                return Collections.emptyList();
            }

            // 0.5x ~ 1.5x
            rand = living.getRandom().nextInt(rand / 2, (int) (rand * 1.5));

            List<ItemStack> addition = xmapFromList(stacks, rand);

            if (living instanceof ServerPlayer player && isSuper.isTrue()) {
                SUPER_LOOT_TRIGGER.trigger(player);
            }

            return addition;
        });
    }

    @NotNull
    private static List<ItemStack> xmapFromList(List<ItemStack> stack, int rand) {
        List<ItemStack> addition = Lists.newArrayList();
        stack.stream().filter(e -> !e.isEmpty()).forEach(s -> {
            ItemStack copy;
            int total = s.getCount() * (1 + rand);
            int maxSize = s.getMaxStackSize();
            if (total <= maxSize) {
                copy = s.copy();
                copy.setCount(total);
                addition.add(copy);
                return;
            }

            for (int i = total; i > 0; i -= copy.getCount()) {
                copy = s.copyWithCount(Math.min(maxSize, i));
                addition.add(copy);
            }
        });
        return addition;
    }

    private int baseIfSuper(int level, double rand, Consumer<Boolean> isSuper) {
        // more loot chance
        if (rand >= getDouble(LOOT_RATE) / 100) {
            return 0;
        }

        // 0.5% only 0
        if (rand < getDouble(SUPER_LOOT_RATE) / 100) {
            level = (int) (level * getDouble(SUPER_LOOT_VALUE));
            isSuper.accept(true);
        }

        return level;
    }

    @Override
    public boolean hasAdv() {
        return true;
    }

    @Override
    public TrueTrigger getAdvTrigger() {
        return SUPER_LOOT_TRIGGER;
    }

    @Override
    public InfoGroupItems collectPlayerInfo(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        int level = level(stack);
        InfoGroupItems group = InfoGroupItems.groupKey(getDescriptionId());
        group.add(getInfoKey(LOOT_RATE), level < 1 ? 0 : getDouble(LOOT_RATE) / 100, true);
        group.add(getInfoKey(SUPER_LOOT_RATE), level < 1 ? 0 : getDouble(SUPER_LOOT_RATE) / 100, true);
        group.add(getInfoKey(SUPER_LOOT_VALUE), level < 1 ? 0 : level * getDouble(SUPER_LOOT_VALUE) * 10, false);
        return group;
    }
}
