package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import net.fabricmc.fabric.api.tool.attribute.v1.ToolManager;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Wearable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

/**
 * 更多战利品
 */
public class MoreLoot extends BaseEnchantment {

    public static final String NAME = "more_loot";

    /**
     * chat tips
     */
    private static final MutableText MORE_LOOT_TEXT = new TranslatableText("enchantment.x_enchant.chat.more_more_loot")
            .setStyle(Style.EMPTY.withColor(Formatting.RED));

    public MoreLoot() {
        super(NAME, Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 1 + (level - 1) * 20;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public void register() {
        super.register();
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return !(stack.getItem() instanceof Wearable);
    }

    @Override
    public UnaryOperator<ItemStack> lootSetter(LivingEntity killer, ItemStack stack, Integer level, Consumer<ItemStack> baseConsumer, LootContext context) {
        // no effect on
        BlockState block = context.get(LootContextParameters.BLOCK_STATE);
        if (block != null && !ToolManager.handleIsEffectiveOn(block, stack, null)) {
            return null;
        }

        // reset count
        String key = "Chat";
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean(nbtKey(key), false);
        return i -> {
            // if is block item, need return
            if (i.getItem() instanceof BlockItem) {
                return i;
            }

            boolean tips = nbt.getBoolean(nbtKey(key));
            MutableBoolean isCrit = new MutableBoolean();
            int rand = rand(level, context.getRandom(), isCrit::setValue);
            if (rand < 1) {
                return i;
            }

            if (killer instanceof ServerPlayerEntity && isCrit.isTrue() && !tips) {
                EnchantUtil.sendMessage((ServerPlayerEntity) killer, stack.getName(), MORE_LOOT_TEXT);
                nbt.putBoolean(nbtKey(key), true);
            }

            if (!i.isStackable()) {
                // isn't stackable only half
                IntStream.range(0, rand / 2).forEach(v -> baseConsumer.accept(i.copy()));
                return i;
            }

            int max = i.getMaxCount();
            int count = i.getCount() * (1 + rand);
            if (count <= max) {
                i.setCount(count);
                return i;
            }
            i.setCount(max);
            count -= max;

            // need new one
            for (; count > 0; count -= max) {
                ItemStack copy = i.copy();
                copy.setCount(Math.min(count, max));
                baseConsumer.accept(copy);
            }
            return i;
        };
    }

    private int rand(int level, Random random, Consumer<Boolean> ifCrit) {
        // 19% only 0, 2-19
        int ran = random.nextInt(100);
        if (ran >= Enchant.option.moreLootRate - 1) {
            return 0;
        }
        ifCrit.accept(false);

        // 1% only 0
        if (ran < Enchant.option.moreMoreLootRate) {
            level *= Enchant.option.moreMoreLootMultiplier;
            ifCrit.accept(true);
        }

        // rand min: 1 ~ level * 1.5
        return Math.max(1, (int) (random.nextInt(level) + level / 2F));
    }
}
