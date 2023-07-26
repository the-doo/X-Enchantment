package com.doo.xenchantment.util;

import com.doo.playerinfo.core.InfoGroupItems;
import com.doo.playerinfo.core.InfoItemCollector;
import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.enchantment.*;
import com.doo.xenchantment.enchantment.curse.DownArmor;
import com.doo.xenchantment.enchantment.curse.DownDamage;
import com.doo.xenchantment.enchantment.curse.Regicide;
import com.doo.xenchantment.enchantment.curse.Thin;
import com.doo.xenchantment.enchantment.special.HealthConverter;
import com.doo.xenchantment.enchantment.special.InfinityEnhance;
import com.doo.xenchantment.enchantment.special.RemoveCursed;
import com.doo.xenchantment.enchantment.special.Special;
import com.doo.xenchantment.events.LootApi;
import com.doo.xenchantment.interfaces.XEnchantmentRegistry;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 附魔工具
 */
public class EnchantUtil {

    public static final List<BaseXEnchantment> ENCHANTMENTS = Lists.newArrayList();

    public static final EquipmentSlot[] ALL_HAND = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};

    private EnchantUtil() {
    }

    /**
     * 注册所有附魔及事件
     */
    public static void configLoad(JsonObject config) {
        if (config == null || config.size() < 1) {
            return;
        }

        ENCHANTMENTS.forEach(e -> Optional.ofNullable(config.getAsJsonObject(e.name)).ifPresent(e::loadOptions));
    }

    /**
     * 注册所有附魔及事件
     */
    public static void registerAll(XEnchantmentRegistry registry) {
        Stream.of(
                        // normal enchantments
                        NightBreak.class, AutoFish.class, SuckBlood.class, Weakness.class, Rebirth.class,
                        MoreLoot.class, HeightAttacked.class, MagicImmune.class,
                        Librarian.class, IncDamage.class, Climber.class, Smart.class,
                        KingKongLegs.class, Diffusion.class, Elasticity.class,
                        BrokenDawn.class, Timor.class, IgnoredArmor.class
                        ,
                        // cursed enchantments
                        Regicide.class, Thin.class, DownDamage.class, DownArmor.class
                        ,
                        // Special enchantments
                        RemoveCursed.class, HealthConverter.class, InfinityEnhance.class
                ).map(BaseXEnchantment::get).filter(Objects::nonNull)
                .sorted(Comparator.comparingInt((BaseXEnchantment e) -> e.isCurse() ? 1 : 0)
                        .thenComparingInt((BaseXEnchantment e) -> e instanceof Special ? 1 : 0)
                        .thenComparingInt((BaseXEnchantment e) -> e.getRarity().getWeight())
                )
                .forEach(e -> {
                    e.register(registry);
                    ENCHANTMENTS.add(e);
                });
    }

    /**
     * 注册所有附魔及事件
     */
    public static void registerPlayerInfo() {
        InfoItemCollector.register(XEnchantment.MOD_NAME, player -> {
            List<InfoGroupItems> sorted = Lists.newArrayList();
            ENCHANTMENTS.stream().filter(e -> !e.disabled())
                    .map(e -> e.collectPlayerInfo(player))
                    .filter(it -> it != null && !it.isEmpty())
                    .forEach(sorted::add);
            return sorted;
        });
    }

    /**
     * 注册所有附魔及事件
     */
    public static void registerAttr(XEnchantmentRegistry registry) {
        ENCHANTMENTS.stream().filter(e -> !e.disabled()).filter(BaseXEnchantment::hasAttr).forEach(registry::register);
    }

    public static void registerAdv(XEnchantmentRegistry registry) {
        ENCHANTMENTS.stream().filter(e -> !e.disabled()).filter(BaseXEnchantment::hasAdv).forEach(registry::register);
    }

    public static void registerToolTips(XEnchantmentRegistry registry) {
        ENCHANTMENTS.stream().filter(e -> !e.disabled()).filter(BaseXEnchantment::needTooltips).forEach(registry::register);
    }

    public static void onClient() {
        ENCHANTMENTS.stream().filter(e -> !e.disabled()).forEach(BaseXEnchantment::onClient);
    }

    public static void onServer() {
        ENCHANTMENTS.stream().filter(e -> !e.disabled()).forEach(BaseXEnchantment::onServer);

        registerPlayerInfo();
    }

    public static void onServerStarted() {
        ENCHANTMENTS.stream().filter(e -> !e.disabled()).forEach(BaseXEnchantment::onServerStarted);
    }

    public static void onKilled(Consumer<BaseXEnchantment> consumer) {
        ENCHANTMENTS.stream().filter(e -> !e.disabled()).forEach(consumer);
    }

    public static void canDeath(Consumer<BaseXEnchantment> consumer) {
        ENCHANTMENTS.stream().filter(e -> !e.disabled()).forEach(consumer);
    }

    public static void endServerTick(LivingEntity entity) {
        ENCHANTMENTS.stream().filter(e -> !e.disabled()).forEach(e -> e.onEndTick(entity));
    }

    public static boolean allowEffectAddition(MobEffectInstance effect, LivingEntity living) {
        MutableBoolean tag = new MutableBoolean(true);
        ENCHANTMENTS.stream().filter(e -> !e.disabled()).forEach(e -> {
            boolean b = e.allowEffectAddition(effect, living);
            if (tag.isTrue()) {
                tag.setValue(b);
            }
        });
        return tag.booleanValue();
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Object o) {
        return (T) o;
    }

    public static ItemStack getHandStack(LivingEntity entity, Class<? extends Item> type, Predicate<ItemStack> test) {
        if (entity != null) {
            ItemStack item = entity.getMainHandItem();
            if (!type.isInstance(item.getItem()) || (test != null && !test.test(item))) {
                item = entity.getOffhandItem();
            }
            return type.isInstance(item.getItem()) && (test == null || test.test(item)) ? item : ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }

    public static void lootMob(DamageSource damageSource, List<ItemStack> additionLoot, Consumer<List<ItemStack>> callback) {
        Entity entity = damageSource.getEntity();
        if (!(entity instanceof Player player)) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        List<ItemStack> trigger = LootApi.trigger(player, stack, additionLoot);
        if (!trigger.isEmpty()) {
            callback.accept(trigger);
        }
    }

    public static void lootFishing(Player player, List<ItemStack> list, Consumer<List<ItemStack>> callback) {
        if (player == null) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        List<ItemStack> trigger = LootApi.trigger(player, stack, list);
        if (!trigger.isEmpty()) {
            callback.accept(trigger);
        }
    }

    public static void lootBlock(Entity entity, ItemStack itemStack, List<ItemStack> list, Consumer<List<ItemStack>> consumer) {
        if (!(entity instanceof LivingEntity player) || list.size() < 2 && list.get(0).getItem() instanceof BlockItem) {
            return;
        }

        List<ItemStack> trigger = LootApi.trigger(player, itemStack, list);
        if (!trigger.isEmpty()) {
            consumer.accept(trigger);
        }
    }

    public static JsonObject getAllOptions() {
        JsonObject object = new JsonObject();
        ENCHANTMENTS.forEach(e -> object.add(e.name, e.getOptions()));
        return object;
    }
}
