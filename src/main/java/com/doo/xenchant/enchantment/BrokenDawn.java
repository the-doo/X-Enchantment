package com.doo.xenchant.enchantment;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Broken Dawn
 * <p>
 * from @BrokenDawn627
 */
public class BrokenDawn extends BaseEnchantment {

    public static final String NAME = "broken_dawn";
    private static final String KEY = "Count";
    private static final String DONE = "Done";

    private static final TranslatableText DONE_TIPS = new TranslatableText("enchantment.x_enchant.broken_dawn.done");
    private static final TranslatableText TIPS = new TranslatableText("enchantment.x_enchant.broken_dawn.tips");

    public BrokenDawn() {
        super(NAME, Rarity.VERY_RARE, EnchantmentTarget.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public int getMinPower(int level) {
        return 1 + (level - 1) * 50;
    }

    @Override
    public int getMaxPower(int level) {
        return level + 150;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        // if it's done
        return !stack.getOrCreateNbt().contains(nbtKey(DONE));
    }

    @Override
    public void itemUsedCallback(LivingEntity owner, ItemStack stack, Integer level, float amount) {
        if (amount < 1) {
            return;
        }

        NbtCompound nbt = stack.getOrCreateNbt();
        long count = nbt.getLong(nbtKey(KEY));
        nbt.putLong(nbtKey(KEY), count += amount);

        // if done
        ifDone(stack, owner.getRandom(), count, owner::dropStack);
    }

    private void ifDone(ItemStack stack, Random random, long count, Consumer<ItemStack> dropper) {
        boolean done = count >= max(stack);
        if (!done) {
            return;
        }
        // log done
        stack.getOrCreateNbt().putBoolean(nbtKey(DONE), true);
        stack.getOrCreateNbt().remove(nbtKey(KEY));

        // default increment
        int inc = 1;
        Item next = nextLevelItem(stack.getItem());
        boolean needLevelUp = random.nextInt(100) < 20;

        // if level up but no next level
        ItemStack drop = ItemStack.EMPTY;
        if (needLevelUp) {
            if (next == Items.AIR) {
                inc *= 3;
            } else {
                drop = next.getDefaultStack();
                drop.setNbt(stack.getNbt());
                drop.setDamage(0);
            }
        }

        // increment all enchantment level
        NbtList enchantments = new NbtList();
        Map<Enchantment, Integer> olds = EnchantmentHelper.get(stack);
        int amount = inc;
        olds.forEach((e, l) -> {
            if (e == this || e.getMaxLevel() < 2) {
                return;
            }

            // increment
            enchantments.add(EnchantmentHelper.createNbt(EnchantmentHelper.getEnchantmentId(e), l + amount));
        });

        if (drop.isEmpty()) {
            // log done
            stack.setSubNbt(ItemStack.ENCHANTMENTS_KEY, enchantments);
            return;
        }

        // remove old
        stack.setDamage(stack.getMaxDamage());

        drop.setSubNbt(ItemStack.ENCHANTMENTS_KEY, enchantments);
        dropper.accept(drop);
    }

    private long max(ItemStack stack) {
        return (long) (stack.getMaxDamage() * 1.5);
    }

    private Item nextLevelItem(Item item) {
        return Registry.ITEM.stream()
                .filter(i -> item.getClass().isInstance(i) && i.getMaxDamage() > item.getMaxDamage())
                .min(Comparator.comparing(Item::getMaxDamage))
                .orElse(Items.AIR);
    }

    @Override
    public void register() {
        super.register();

        // tooltips
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ItemTooltipCallback.EVENT.register(((stack, context, lines) -> {
                NbtCompound nbt = stack.getOrCreateNbt();
                // if done
                if (nbt.getBoolean(nbtKey(DONE))) {
                    lines.add(DONE_TIPS.formatted(Formatting.GOLD));
                    lines.add(new TranslatableText(getTranslationKey()).append(" - ").append(TIPS).formatted(Formatting.GRAY));
                    return;
                }

                // not done
                if (level(stack) > 0) {
                    lines.add(new TranslatableText(getTranslationKey()).append(": ")
                            .append(FORMAT.format(10D * nbt.getLong(nbtKey(KEY)) / max(stack)) + "%").formatted(Formatting.GRAY));
                    lines.add(new TranslatableText(getTranslationKey()).append(" - ")
                            .append(TIPS).formatted(Formatting.GRAY));
                }
            }));
        }
    }
}
