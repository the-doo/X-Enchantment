package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.core.InfoGroupItems;
import com.doo.xenchantment.events.AnvilApi;
import com.doo.xenchantment.events.GrindstoneApi;
import com.doo.xenchantment.interfaces.OneLevelMark;
import com.doo.xenchantment.interfaces.Tooltipsable;
import com.doo.xenchantment.interfaces.Usable;
import com.doo.xenchantment.util.EnchantUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class WithEffect extends BaseXEnchantment implements
        Tooltipsable<WithEffect>, Usable<WithEffect>, OneLevelMark {

    private static final String EFFECT_KEY_TAG = "effect";

    private static final String BAN_KEY = "ban";
    private static final String LEVEL_KEY = "level";
    private static final String DURATION_KEY = "duration";

    private static final Map<String, MobEffect> BAN_MAP = new LinkedHashMap<>();

    private static final Map<String, MobEffect> EFFECT_MAP = new LinkedHashMap<>();

    private int durationTick;

    public WithEffect() {
        super("with_effect", Rarity.RARE, EnchantmentCategory.ARMOR, EnchantUtil.ALL_ARMOR);

        options.add(BAN_KEY, new JsonArray());
        options.addProperty(LEVEL_KEY, 2);
        options.addProperty(DURATION_KEY, 3);

        durationTick = 3 * SECOND_TICK;
    }

    public static void removeIfEq(CompoundTag tag, ItemStack off) {
        BaseXEnchantment e = EnchantUtil.ENCHANTMENTS_MAP.get(WithEffect.class);
        if (e.getId().equals(EnchantmentHelper.getEnchantmentId(tag))) {
            off.removeTagKey(e.nbtKey(EFFECT_KEY_TAG));
        }
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, BAN_KEY);
        loadIf(json, LEVEL_KEY);
        loadIf(json, DURATION_KEY);

        BAN_MAP.clear();
        foreach(BAN_KEY, e -> streamEffect(effect -> effect.getDescriptionId().equals(e.getAsString()))
                .forEach(effect -> BAN_MAP.put(effect.getDescriptionId(), effect)));

        durationTick = (int) (SECOND_TICK * doubleV(DURATION_KEY));
    }

    @Override
    public void onOptionsRegister(BiConsumer<String, Supplier<Stream<String>>> register) {
        super.onOptionsRegister(register);

        register.accept(BAN_KEY, () -> streamEffect(null).map(MobEffect::getDescriptionId));
    }

    @Override
    public void onServer(MinecraftServer server) {
        String key = nbtKey(EFFECT_KEY_TAG);
        GrindstoneApi.register(stack -> stack.removeTagKey(key));

        AnvilApi.register((player, map, first, second, result) -> {
            if (!map.containsKey(this) || result.getTag() == null) {
                return;
            }

            Map<Enchantment, Integer> m;
            if (result.getTag().contains(key) && second.is(Items.ENCHANTED_BOOK) && (m = EnchantmentHelper.getEnchantments(second)).containsKey(this)) {
                result.removeTagKey(key);

                if (m.size() < 2) {
                    result.getTag().putInt("RepairCost", first.getTag().getInt("RepairCost"));
                }
            }
        });

        EFFECT_MAP.clear();
        streamEffect(null).forEach(e -> EFFECT_MAP.put(e.getDescriptionId(), e));
    }

    @Override
    public void onEndTick(LivingEntity living) {
        if (durationTick < 1 || living.tickCount % durationTick != 0) {
            return;
        }

        // trigger
        String key = nbtKey(EFFECT_KEY_TAG);
        living.getArmorSlots().forEach(stack -> {
            if (level(stack) < 1) {
                return;
            }

            String id = stack.getTag().getString(key);
            MobEffect effect;
            if (BAN_MAP.containsKey(id) || (effect = EFFECT_MAP.get(id)) == null) {
                return;
            }

            addEffect(living, effect);
        });
    }

    private void addEffect(LivingEntity living, MobEffect effect) {
        int level = intV(LEVEL_KEY) - 1;
        if (level < 0) {
            return;
        }

        int duration = durationTick + 10 + (effect == MobEffects.NIGHT_VISION ? 12 * SECOND_TICK : 0);
        MobEffectInstance instance = new MobEffectInstance(effect, duration, level);
        if (living.hasEffect(effect)) {
            living.getEffect(effect).update(instance);
        } else {
            living.addEffect(instance);
        }
    }

    @Override
    public void onEquipItem(Integer level, LivingEntity living, EquipmentSlot slot, ItemStack stack) {
        String key = nbtKey(EFFECT_KEY_TAG);
        if (stack.getTag().contains(key)) {
            return;
        }

        List<MobEffect> list = streamEffect(e -> !BAN_MAP.containsKey(e.getDescriptionId())).toList();
        if (list.isEmpty()) {
            return;
        }

        MobEffect effect = list.get(living.getRandom().nextInt(list.size()));
        stack.getTag().putString(nbtKey(EFFECT_KEY_TAG), effect.getDescriptionId());

        addEffect(living, effect);
    }

    private Stream<MobEffect> streamEffect(Predicate<MobEffect> filter) {
        Stream<MobEffect> stream = BuiltInRegistries.MOB_EFFECT.stream()
                .filter(e -> e.getCategory() == MobEffectCategory.BENEFICIAL && !e.isInstantenous());

        return filter == null ? stream : stream.filter(filter);
    }

    @Override
    public void tooltip(ItemStack stack, TooltipFlag context, List<Component> lines) {
        CompoundTag tag;
        if (disabled() || stack.getItem() instanceof EnchantedBookItem || (tag = stack.getTag()) == null || tag.isEmpty()) {
            return;
        }

        if (level(stack) < 1) {
            return;
        }

        String key = tag.getString(nbtKey(EFFECT_KEY_TAG));
        if (key.isEmpty()) {
            return;
        }

        MutableComponent component = getFullname(1).copy().append(": ").append(Component.translatable(key));
        if (BAN_MAP.containsKey(key)) {
            component.withStyle(ChatFormatting.STRIKETHROUGH);
        }
        lines.add(component.withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public InfoGroupItems collectPlayerInfo(ServerPlayer player) {
        InfoGroupItems group = InfoGroupItems.groupKey(getDescriptionId());
        group.add(getInfoKey(LEVEL_KEY), intV(LEVEL_KEY));
        group.add(getInfoKey(DURATION_KEY), doubleV(DURATION_KEY));

        Arrays.stream(slots).forEach(slot -> {
            ItemStack stack = player.getItemBySlot(slot);
            if (level(stack) < 1) {
                return;
            }
            String key = stack.getTag().getString(nbtKey(EFFECT_KEY_TAG));
            if (key.isEmpty()) {
                return;
            }
            group.add(getInfoKey(slot.getName()), Component.translatable(key).getString());
        });
        return group;
    }
}
