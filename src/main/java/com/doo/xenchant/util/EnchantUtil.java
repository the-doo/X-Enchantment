package com.doo.xenchant.util;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.enchantment.*;
import com.doo.xenchant.enchantment.curse.DownArmor;
import com.doo.xenchant.enchantment.curse.DownDamage;
import com.doo.xenchant.enchantment.curse.Regicide;
import com.doo.xenchant.enchantment.curse.Thin;
import com.doo.xenchant.enchantment.halo.AttrHalo;
import com.doo.xenchant.enchantment.halo.EffectHalo;
import com.doo.xenchant.enchantment.halo.HeightAdvantageHalo;
import com.doo.xenchant.enchantment.halo.ThunderHalo;
import com.doo.xenchant.enchantment.special.HealthConverter;
import com.doo.xenchant.enchantment.special.InfinityEnhance;
import com.doo.xenchant.enchantment.special.RemoveCursed;
import com.doo.xenchant.enchantment.trinkets.Trinkets;
import com.doo.xenchant.events.LivingApi;
import com.google.common.collect.Maps;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 附魔工具
 */
public class EnchantUtil {

    public static boolean hasTrinkets = false;

    public static boolean hasFTBTeam = false;

    /**
     * 所有盔甲
     */
    public static final EquipmentSlot[] ALL_ARMOR = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    /**
     * ALL HAND
     */
    public static final EquipmentSlot[] ALL_HAND = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};

    private EnchantUtil() {
    }

    /**
     * 注册所有附魔及事件
     */
    public static void registerAll() {
        // normal enchantments
        Stream<Class<? extends BaseEnchantment>> stream = Stream.of(AutoFish.class, SuckBlood.class, Weakness.class, Rebirth.class,
                MoreLoot.class, HitRateUp.class, QuickShot.class, MagicImmune.class,
                Librarian.class, IncDamage.class, Climber.class, Smart.class,
                KingKongLegs.class, Diffusion.class, Elasticity.class,
                NightBreak.class, BrokenDawn.class, Timor.class);
        processStream(stream);

        // cursed enchantments
        stream = Stream.of(Regicide.class, Thin.class, DownDamage.class, DownArmor.class);
        processStream(stream);

        // Special enchantments
        if (Enchant.option.special) {
            stream = Stream.of(RemoveCursed.class, HealthConverter.class, InfinityEnhance.class);
            processStream(stream);
        }

        // Trinkets enchantments
        if (hasTrinkets && Enchant.option.trinkets) {
            regisTrinkets();
        }

        // Halo enchantments
        if (Enchant.option.halo) {
            stream = Stream.of(ThunderHalo.class, HeightAdvantageHalo.class);
            processStream(stream);

            // regis to server
            ServerLifecycleEvents.SERVER_STARTED.register(server -> {
                regisEffect();

                regisAttr();
            });

            // regis to client
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
                    regisEffect();

                    regisAttr();
                });
            }
        }
    }

    private static void processStream(Stream<Class<? extends BaseEnchantment>> stream) {
        stream.filter(c -> !Enchant.option.disabled.contains(c.getName()))
                .map(c -> {
                    try {
                        return c.newInstance();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(e -> ((BaseEnchantment) e).getRarity().getWeight()).reversed())
                .forEach(BaseEnchantment::register);
    }

    private static void regisAttr() {
        // Status effect halo must regis after all mod loaded
        // need filter(s -> Identifier.isValid(s.getTranslationKey()))
        // if not exists
        Registry.ATTRIBUTE.stream()
                .filter(e -> Enchant.option.attributes.contains(e.getTranslationKey()))
                .map(AttrHalo::new)
                .sorted(Comparator.comparing(e -> ((BaseEnchantment) e).getRarity().getWeight()).reversed())
                .forEach(BaseEnchantment::register);
    }

    private static void regisEffect() {
        // Attribute halo must regis after all mod loaded
        // need filter(s -> Identifier.isValid(s.getTranslationKey()))
        // if not exists
        Registry.STATUS_EFFECT.stream()
                .filter(e -> e != null && Identifier.isValid(e.getTranslationKey()) && !Enchant.option.disabledEffect.contains(e.getTranslationKey()))
                .filter(e -> !Enchant.option.onlyPotionEffect || Registry.POTION.stream().anyMatch(p -> p.getEffects().contains(e)))
                .map(EffectHalo::new)
                .sorted(Comparator.comparing(e -> ((BaseEnchantment) e).getRarity().getWeight()).reversed())
                .forEach(BaseEnchantment::register);
    }

    private static void regisTrinkets() {
        // Trinkets
        Arrays.stream(Trinkets.Attrs.values())
                .map(Trinkets::new)
                .sorted(Comparator.comparing(e -> ((BaseEnchantment) e).getRarity().getWeight()).reversed())
                .forEach(BaseEnchantment::register);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Object o) {
        return (T) o;
    }

    public static ItemStack getHandStack(LivingEntity entity, Class<? extends Item> type, Predicate<ItemStack> test) {
        if (entity != null) {
            ItemStack item = entity.getStackInHand(Hand.MAIN_HAND);
            if (!type.isInstance(item.getItem()) || (test != null && !test.test(item))) {
                item = entity.getStackInHand(Hand.OFF_HAND);
            }
            return type.isInstance(item.getItem()) && (test == null || test.test(item)) ? item : ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }

    public static int useTime(int time, LivingEntity entity, ItemStack stack) {
        return time - LivingApi.REDUCE_USE_TIME.invoker().get(entity, stack);
    }

    /**
     * Get enchantments on merge
     */
    public static Map<BaseEnchantment, Pair<Integer, Integer>> mergeOf(LivingEntity living) {
        List<ItemStack> temps = new ArrayList<>();
        living.getItemsEquipped().forEach(temps::add);
        if (hasTrinkets) {
            TrinketsApi.getTrinketComponent(living).ifPresent(c -> c.getAllEquipped().forEach(p -> temps.add(p.getRight())));
        }


        Map<BaseEnchantment, Pair<Integer, Integer>> map = Maps.newHashMap();
        temps.stream().filter(ItemStack::hasEnchantments)
                .flatMap(i -> EnchantmentHelper.get(i).entrySet().stream())
                .forEach(e -> {
                    if (e.getKey() instanceof BaseEnchantment && e.getValue() != null && e.getValue() > 0) {
                        map.compute((BaseEnchantment) e.getKey(), (k, v) -> {
                            int level = e.getValue();
                            v = v == null ? new Pair<>(0, 0) : v;

                            // total level
                            v.setLeft(v.getLeft() + level);

                            // max level
                            if (v.getRight() < level) {
                                v.setLeft(v.getLeft() + level);
                            }
                            return v;
                        });
                    }
                });
        return map;
    }

    public static Optional<Enchantment> rand(Enchantment.Rarity rarity, Random random) {
        List<Enchantment> list = Registry.ENCHANTMENT.stream()
                .filter(e -> (rarity == null || e.getRarity() == rarity) && e.isAvailableForRandomSelection())
                .collect(Collectors.toList());

        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(random.nextInt(list.size())));
    }
}
