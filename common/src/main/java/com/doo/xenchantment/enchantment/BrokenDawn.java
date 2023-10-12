package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.core.InfoGroupItems;
import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.advancements.TrueTrigger;
import com.doo.xenchantment.events.ItemApi;
import com.doo.xenchantment.interfaces.Advable;
import com.doo.xenchantment.interfaces.OneLevelMark;
import com.doo.xenchantment.interfaces.Tooltipsable;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Broken Dawn
 * <p>
 * from @BrokenDawn627
 */
public class BrokenDawn extends BaseXEnchantment implements
        Tooltipsable<BrokenDawn>, Advable<BrokenDawn>, OneLevelMark {
    private static final String LEVEL_UP_KEY = "level_up_rate";
    private static final String DONE_LIMIT_KEY = "done_limit";
    private static final String LEVEL_UP_COUNT_INFO_KEY = "level_up_count";
    private static final String LEVEL_UP_ITEM_INFO_KEY = "level_up_item";
    private static final String DONE_KEY = "done";
    private static final String TIP_KEY = "tip";
    private static final String KEY = "Count";
    private static final String DONE = "Done";

    public static final TrueTrigger DAWN_COMING =
            TrueTrigger.get(new ResourceLocation(XEnchantment.MOD_ID, "trigger.broken_dawn.dawn_coming"));

    private static final Component DONE_TIPS = Component.translatable("enchantment.x_enchantment.broken_dawn.done")
            .withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GOLD);
    private static final Component TIPS = Component.literal(" - ").withStyle(ChatFormatting.DARK_GRAY)
            .append(Component.translatable("enchantment.x_enchantment.broken_dawn.tips"));

    public static final List<Function<ItemStack, Predicate<Item>>> ITEM_SWITCHERS = Lists.newArrayList();

    public BrokenDawn() {
        super("broken_dawn", Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.values());

        DONE_TIPS.getStyle().withColor(ChatFormatting.GOLD);

        options.addProperty(LEVEL_UP_KEY, 20);
        options.addProperty(DONE_LIMIT_KEY, 1.5);
        options.addProperty(TIP_KEY, true);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, LEVEL_UP_KEY);
        loadIf(json, DONE_LIMIT_KEY);
        loadIf(json, TIP_KEY);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return !stack.getOrCreateTag().contains(nbtKey(DONE));
    }

    @Override
    public @NotNull Component getFullname(int level) {
        if (!boolV(TIP_KEY)) {
            return super.getFullname(level);
        }
        return super.getFullname(level).copy().append(TIPS);
    }

    @Override
    public void onServer(MinecraftServer server) {
        ItemApi.register((owner, stack, amount) -> {
            if (disabled() || level(stack) < 1) {
                return;
            }

            CompoundTag nbt = stack.getOrCreateTag();
            String key = nbtKey(KEY);
            long count = nbt.getLong(key);
            nbt.putLong(key, (long) (count + randomAmount(owner, amount)));

            // if done
            ifDone(stack, owner, count, owner::spawnAtLocation);
        });
    }

    private static float randomAmount(LivingEntity owner, float amount) {
        if (owner == null) {
            return amount;
        }

        RandomSource random = owner.getRandom();
        return random.nextDouble() < 0.6 ? amount : amount * random.nextInt(1, 4);
    }

    @Override
    public void tooltip(ItemStack stack, TooltipFlag context, List<Component> lines) {
        CompoundTag nbt = stack.getTag();
        if (stack.getItem() instanceof EnchantedBookItem || nbt == null || nbt.isEmpty()) {
            return;
        }

        // if done
        if (nbt.getBoolean(nbtKey(DONE))) {
            lines.add(DONE_TIPS.copy().append(TIPS));
            return;
        }

        if (level(stack) < 1) {
            return;
        }

        // not done
        double process = max(stack);
        process = process <= 0 ? 99.99 : 100D * nbt.getLong(nbtKey(KEY)) / process;
        lines.add(Component.translatable(getDescriptionId()).append(" - ").append(FORMAT.format(process) + "%")
                .withStyle(ChatFormatting.GRAY));
    }

    private void ifDone(ItemStack stack, LivingEntity owner, long count, Consumer<ItemStack> dropper) {
        boolean done = count >= max(stack);
        if (!done) {
            return;
        }
        // log done
        stack.getOrCreateTag().putBoolean(nbtKey(DONE), true);
        stack.getOrCreateTag().remove(nbtKey(KEY));

        // default increment
        RandomSource random = owner.getRandom();
        int inc = 1;
        boolean needLevelUp = random.nextDouble() < doubleV(LEVEL_UP_KEY) / 100;
        ItemStack drop = ItemStack.EMPTY;
        if (needLevelUp) {
            Item next = nextLevelItem(stack);
            // need level up but hasn't next level
            if (next == Items.AIR) {
                inc *= 3;
            } else {
                drop = next.getDefaultInstance();
                drop.setTag(stack.getTag());
                drop.setDamageValue(0);
            }
            // trigger
            if (owner instanceof ServerPlayer player) {
                DAWN_COMING.trigger(player);
            }
        }

        // increment all enchantment level
        ListTag enchantments = new ListTag();
        Map<Enchantment, Integer> olds = EnchantmentHelper.getEnchantments(stack);
        olds.remove(this);

        int amount = inc;
        olds.forEach((e, l) ->
                enchantments.add(EnchantmentHelper.storeEnchantment(EnchantmentHelper.getEnchantmentId(e), l + (e == this || e.getMaxLevel() < 2 ? 0 : amount))));

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
        return (long) (stack.getMaxDamage() * doubleV(DONE_LIMIT_KEY));
    }

    private Item nextLevelItem(ItemStack stack) {
        return BuiltInRegistries.ITEM.stream()
                .filter(switchFilter(stack))
                .min(Comparator.comparing(Item::getMaxDamage))
                .orElse(Items.AIR);
    }

    private Predicate<Item> switchFilter(ItemStack stack) {
        for (Function<ItemStack, Predicate<Item>> switcher : ITEM_SWITCHERS) {
            Predicate<Item> predicate = switcher.apply(stack);
            if (predicate != null) {
                return predicate;
            }
        }

        // default
        Item item = stack.getItem();
        if (item instanceof ArmorItem ia) {
            return i -> i instanceof ArmorItem ai && ia.getEquipmentSlot() == ai.getEquipmentSlot() &&
                    ai.getMaxDamage() > ia.getMaxDamage() && ai.getDefense() > ia.getDefense();
        }
        if (item instanceof ShovelItem it) {
            return i -> i instanceof ShovelItem ti && isToolUp(it, ti);
        }
        if (item instanceof HoeItem it) {
            return i -> i instanceof HoeItem ti && isToolUp(it, ti);
        }
        if (item instanceof AxeItem it) {
            return i -> i instanceof AxeItem ti && isToolUp(it, ti);
        }
        if (item instanceof PickaxeItem it) {
            return i -> i instanceof PickaxeItem ti && isToolUp(it, ti);
        }
        if (item instanceof SwordItem is) {
            return i -> i instanceof SwordItem si && si.getDamage() > is.getDamage() && si.getMaxDamage() > is.getMaxDamage();
        }
        if (item instanceof TieredItem it) {
            return i -> i instanceof TieredItem ti && isToolUp(it, ti);
        }
        return i -> item.getClass().isInstance(i) && i.getMaxDamage() > item.getMaxDamage();
    }

    private static boolean isToolUp(TieredItem it, TieredItem ti) {
        return ti.getMaxDamage() > it.getMaxDamage() &&
                (ti.getTier().getLevel() > it.getTier().getLevel() || ti.getTier().getUses() > it.getTier().getUses() && ti.getTier().getSpeed() > it.getTier().getSpeed());
    }

    @Override
    public InfoGroupItems collectPlayerInfo(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        boolean notLevel = stack.getTag() == null || stack.getTag().isEmpty() || level(stack) < 1;
        InfoGroupItems group = InfoGroupItems.groupKey(getDescriptionId());
        if (!notLevel && stack.getOrCreateTag().getBoolean(nbtKey(DONE))) {
            group.add(getInfoKey(DONE_KEY), DONE_TIPS.getString(), false);
            return group;
        }

        group.add(getInfoKey(LEVEL_UP_COUNT_INFO_KEY), notLevel ? 0 : stack.getOrCreateTag().getLong(nbtKey(KEY)), false);
        group.add(getInfoKey(DONE_LIMIT_KEY), notLevel ? 0 : max(stack), false);
        group.add(getInfoKey(LEVEL_UP_KEY), notLevel ? 0 : doubleV(LEVEL_UP_KEY) / 100, true);

        Item i;
        group.add(getInfoKey(LEVEL_UP_ITEM_INFO_KEY),
                notLevel || (i = nextLevelItem(stack)) == Items.AIR ? "None" : i.getDefaultInstance().getDisplayName().getString(), false);
        return group;
    }

    @Override
    public TrueTrigger getAdvTrigger() {
        return DAWN_COMING;
    }
}
