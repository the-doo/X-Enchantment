package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.ItemApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Broken Dawn
 * <p>
 * from @BrokenDawn627
 */
public class BrokenDawn extends BaseEnchantment {

    public static final String NAME = "broken_dawn";
    private static final String KEY = "Count";
    private static final String DONE = "Done";

    private static final AttributeModifier DAMAGE = new AttributeModifier("", 1, AttributeModifier.Operation.MULTIPLY_TOTAL);

    private static final TranslatableComponent DONE_TIPS = new TranslatableComponent("enchantment.x_enchant.broken_dawn.done");
    private static final TranslatableComponent TIPS = new TranslatableComponent("enchantment.x_enchant.broken_dawn.tips");

    public BrokenDawn() {
        super(NAME, Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        // if it's done
        return !stack.getOrCreateTag().contains(nbtKey(DONE));
    }

    @Override
    public void register() {
        super.register();

        ItemApi.WILL_DAMAGE.register(((owner, stack, amount) -> {
            if (!Enchant.option.brokenDawn || amount < 1 || owner == null || owner.level.isClientSide()) {
                return;
            }

            int level = level(stack);
            if (level < 1) {
                return;
            }

            CompoundTag nbt = stack.getOrCreateTag();
            long count = nbt.getLong(nbtKey(KEY));
            nbt.putLong(nbtKey(KEY), count += amount);

            // if done
            ifDone(stack, owner.getRandom(), count, owner::spawnAtLocation);
        }));

        // if done
        ItemApi.GET_MODIFIER.register(((map, stack, slot) -> {
            if (stack.getOrCreateTag().getBoolean(nbtKey(DONE)) && slot == EquipmentSlot.MAINHAND) {
                map.put(Attributes.ATTACK_DAMAGE, DAMAGE);
            }
        }));

        // tooltips
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && Enchant.option.brokenDawn) {
            ItemTooltipCallback.EVENT.register(((stack, context, lines) -> {
                if (stack.getItem() instanceof EnchantedBookItem) {
                    return;
                }

                CompoundTag nbt = stack.getOrCreateTag();
                // if done
                if (nbt.getBoolean(nbtKey(DONE))) {
                    lines.add(DONE_TIPS.withStyle(ChatFormatting.GOLD));
                    lines.add(new TranslatableComponent(getDescriptionId()).append(" - ").append(TIPS).withStyle(ChatFormatting.GRAY));
                    return;
                }

                // not done
                if (level(stack) > 0) {
                    lines.add(new TranslatableComponent(getDescriptionId()).append(": ").append(FORMAT.format(100D * nbt.getLong(nbtKey(KEY)) / max(stack)) + "%")
                            .withStyle(ChatFormatting.GRAY));
                    lines.add(new TranslatableComponent(getDescriptionId()).append(" - ").append(TIPS)
                            .withStyle(ChatFormatting.GRAY));
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
        stack.getOrCreateTag().putBoolean(nbtKey(DONE), true);
        stack.getOrCreateTag().remove(nbtKey(KEY));

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
                drop = next.getDefaultInstance();
                drop.setTag(stack.getTag());
                drop.setDamageValue(0);
            }
        }

        // increment all enchantment level
        ListTag enchantments = new ListTag();
        Map<Enchantment, Integer> olds = EnchantmentHelper.getEnchantments(stack);
        int amount = inc;
        olds.forEach((e, l) -> {
            // increment
            enchantments.add(EnchantmentHelper.storeEnchantment(EnchantmentHelper.getEnchantmentId(e), l + (e == this || e.getMaxLevel() < 2 ? 0 : amount)));
        });

        if (drop.isEmpty()) {
            // log done
            stack.addTagElement(ItemStack.TAG_ENCH, enchantments);
            return;
        }

        // remove old
        stack.setDamageValue(stack.getMaxDamage());

        drop.addTagElement(ItemStack.TAG_ENCH, enchantments);
        dropper.accept(drop);
    }

    private long max(ItemStack stack) {
        return (long) (stack.getMaxDamage() * Enchant.option.brokenDawnProcess);
    }

    private Item nextLevelItem(Item item) {
        Predicate<Item> filter = switchFilter(item);
        return Registry.ITEM.stream().filter(filter).min(Comparator.comparing(Item::getMaxDamage)).orElse(Items.AIR);
    }

    private Predicate<Item> switchFilter(Item item) {
        if (item instanceof ArmorItem) {
            return i -> i instanceof ArmorItem && ((ArmorItem) item).getSlot() == ((ArmorItem) i).getSlot() && i.getMaxDamage() > item.getMaxDamage();
        }
        if (item instanceof SwordItem) {
            return i -> i instanceof SwordItem && ((SwordItem) item).getDamage() > ((SwordItem) i).getDamage();
        }
        if (item instanceof DiggerItem) {
            return i -> i instanceof DiggerItem &&
                    (((DiggerItem) item).getTier().getLevel() > ((DiggerItem) i).getTier().getLevel() ||
                            ((DiggerItem) item).getTier().getSpeed() > ((DiggerItem) i).getTier().getSpeed());
        }
        return i -> item.getClass().isInstance(i) && i.getMaxDamage() > item.getMaxDamage();
    }
}
