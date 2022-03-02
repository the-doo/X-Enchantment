package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.ItemApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.EnchantedBookItem;
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

    private static final EntityAttributeModifier DAMAGE = new EntityAttributeModifier("", 1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

    private static final TranslatableText DONE_TIPS = new TranslatableText("enchantment.x_enchant.broken_dawn.done");
    private static final TranslatableText TIPS = new TranslatableText("enchantment.x_enchant.broken_dawn.tips");

    public BrokenDawn() {
        super(NAME, Rarity.VERY_RARE, EnchantmentTarget.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        // if it's done
        return !stack.getOrCreateNbt().contains(nbtKey(DONE));
    }

    @Override
    public void register() {
        super.register();

        ItemApi.WILL_DAMAGE.register(((owner, stack, amount) -> {
            if (!Enchant.option.brokenDawn || amount < 1 || owner == null || owner.world.isClient()) {
                return;
            }

            int level = level(stack);
            if (level < 1) {
                return;
            }

            NbtCompound nbt = stack.getOrCreateNbt();
            long count = nbt.getLong(nbtKey(KEY));
            nbt.putLong(nbtKey(KEY), count += amount);

            // if done
            ifDone(stack, owner.getRandom(), count, owner::dropStack);
        }));

        // if done
        ItemApi.GET_MODIFIER.register(((map, stack, slot) -> {
            if (stack.getOrCreateNbt().getBoolean(nbtKey(DONE)) && slot == EquipmentSlot.MAINHAND) {
                map.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, DAMAGE);
            }
        }));

        // tooltips
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && Enchant.option.brokenDawn) {
            ItemTooltipCallback.EVENT.register(((stack, context, lines) -> {
                if (stack.getItem() instanceof EnchantedBookItem && level(stack) > 0) {
                    lines.add(TIPS.formatted(Formatting.GRAY));
                    return;
                }

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
                            .append(FORMAT.format(100D * nbt.getLong(nbtKey(KEY)) / max(stack)) + "%").formatted(Formatting.GRAY));
                    lines.add(new TranslatableText(getTranslationKey()).append(" - ")
                            .append(TIPS).formatted(Formatting.GRAY));
                }
            }));
        }
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
        boolean needLevelUp = random.nextInt(100) < Enchant.option.brokenDawnSuccess;

        // need level up but hasn't next level
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
        return (long) (stack.getMaxDamage() * Enchant.option.brokenDawnProcess);
    }

    private Item nextLevelItem(Item item) {
        return Registry.ITEM.stream()
                .filter(i -> item.getClass().isInstance(i) && i.getMaxDamage() > item.getMaxDamage())
                .min(Comparator.comparing(Item::getMaxDamage))
                .orElse(Items.AIR);
    }
}
