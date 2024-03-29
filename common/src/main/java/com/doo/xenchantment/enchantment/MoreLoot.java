package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.core.InfoGroupItems;
import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.advancements.TrueTrigger;
import com.doo.xenchantment.events.LootApi;
import com.doo.xenchantment.interfaces.Advable;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.DropExperienceBlock;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class MoreLoot extends BaseXEnchantment implements Advable<MoreLoot> {

    public static final TrueTrigger SUPER_LOOT_TRIGGER =
            TrueTrigger.get(new ResourceLocation(XEnchantment.MOD_ID, "trigger.more_loot.super_loot"));

    public static final String EFFECT_KEY = "effect_ore";
    public static final String LOOT_RATE = "loot_rate";
    public static final String TIP_ON = "tips";
    public static final String SUPER_LOOT_RATE = "super_loot_rate";
    public static final String SUPER_LOOT_VALUE = "super_loot_value";

    private static final Component TRIGGER_TIP = Component.translatable("x_enchantment.more_loot.tips.trigger");
    private static final Component SUP_TRIGGER_TIP = Component.translatable("x_enchantment.more_loot.tips.super_trigger");

    public MoreLoot() {
        super("more_loot", Rarity.RARE, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);

        options.addProperty(MAX_LEVEL_KEY, 5);
        options.addProperty(EFFECT_KEY, false);
        options.addProperty(TIP_ON, true);
        options.addProperty(LOOT_RATE, 20);
        options.addProperty(SUPER_LOOT_RATE, 5);
        options.addProperty(SUPER_LOOT_VALUE, 10);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, TIP_ON);
        loadIf(json, EFFECT_KEY);
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
        LootApi.register((living, random, stack, stacks, effectBlocks) -> {
            if (disabled()) {
                return Collections.emptyList();
            }

            int level = level(stack);
            if (level < 1) {
                return Collections.emptyList();
            }

            MutableBoolean isSuper = new MutableBoolean();
            int rand = baseIfSuper(level, random.nextDouble(), isSuper::setValue);
            if (rand < 1) {
                return Collections.emptyList();
            }

            // 0.5x ~ 1.5x
            rand = random.nextInt(rand / 2, (int) (rand * 1.5));

            List<ItemStack> addition = Lists.newArrayList();
            stacks.stream()
                    .filter(ItemStack::isStackable)
                    .filter(i -> effectBlocks || !(i.getItem() instanceof BlockItem bi) || boolV(EFFECT_KEY) && bi.getBlock() instanceof DropExperienceBlock)
                    .forEach(addFromForeach(rand, addition::add));

            if (addition.isEmpty() || !(living instanceof Player p)) {
                return addition;
            }

            if (boolV(TIP_ON)) {
                p.displayClientMessage(isSuper.isTrue() ? SUP_TRIGGER_TIP : TRIGGER_TIP, true);
            }

            if (isSuper.isTrue() && living instanceof ServerPlayer player) {
                SUPER_LOOT_TRIGGER.trigger(player);
            }

            return addition;
        });
    }

    @NotNull
    private static Consumer<ItemStack> addFromForeach(int rand, Consumer<ItemStack> addition) {
        return s -> {
            ItemStack copy;
            int total = s.getCount() * (1 + rand);
            int maxSize = s.getMaxStackSize();
            if (total <= maxSize) {
                copy = s.copy();
                copy.setCount(total);
                addition.accept(copy);
                return;
            }

            for (int i = total; i > 0; i -= copy.getCount()) {
                copy = s.copyWithCount(Math.min(maxSize, i));
                addition.accept(copy);
            }
        };
    }

    private int baseIfSuper(int level, double rand, Consumer<Boolean> isSuper) {
        // more loot chance
        if (rand >= doubleV(LOOT_RATE) / 100) {
            return 0;
        }

        // 0.5% only 0
        if (rand < doubleV(SUPER_LOOT_RATE) / 1000) {
            level = (int) (level * doubleV(SUPER_LOOT_VALUE));
            isSuper.accept(true);
        }

        return level;
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
        group.add(getInfoKey(LOOT_RATE), level < 1 ? 0 : doubleV(LOOT_RATE) / 100, true);
        group.add(getInfoKey(SUPER_LOOT_RATE), level < 1 ? 0 : doubleV(SUPER_LOOT_RATE) / 100, true);
        group.add(getInfoKey(SUPER_LOOT_VALUE), level < 1 ? 0 : level * doubleV(SUPER_LOOT_VALUE) * 10, false);
        return group;
    }
}
