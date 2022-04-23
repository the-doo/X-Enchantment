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
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

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

            regisEffect();

            regisAttr();
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
                .filter(e -> Enchant.option.attributes.contains(e.getDescriptionId()))
                .map(AttrHalo::new)
                .sorted(Comparator.comparing(e -> ((BaseEnchantment) e).getRarity().getWeight()).reversed())
                .forEach(BaseEnchantment::register);
    }

    private static void regisEffect() {
        Set<MobEffect> effects = Registry.POTION.stream()
                .flatMap(p -> p.getEffects().stream())
                .map(MobEffectInstance::getEffect)
                .collect(Collectors.toSet());
        // Attribute halo must regis after all mod loaded
        // need filter(s -> Identifier.isValid(s.getTranslationKey()))
        // if not exists
        Registry.MOB_EFFECT.stream()
                .filter(e -> e != null && ResourceLocation.isValidResourceLocation(e.getDescriptionId()) && !Enchant.option.disabledEffect.contains(e.getDescriptionId()))
                .filter(e -> !Enchant.option.onlyPotionEffect || effects.contains(e))
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
            ItemStack item = entity.getMainHandItem();
            if (!type.isInstance(item.getItem()) || (test != null && !test.test(item))) {
                item = entity.getOffhandItem();
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
     *
     * @return map(enchantment - > ( total, max))
     */
    public static Map<BaseEnchantment, Tuple<Integer, Integer>> mergeOf(LivingEntity living) {
        List<ItemStack> temps = new ArrayList<>();
        living.getAllSlots().forEach(temps::add);
        if (hasTrinkets) {
            TrinketsApi.getTrinketComponent(living).ifPresent(c -> c.getAllEquipped().forEach(p -> temps.add(p.getB())));
        }

        Map<BaseEnchantment, Tuple<Integer, Integer>> map = Maps.newHashMap();
        temps.stream().filter(ItemStack::isEnchanted)
                .flatMap(i -> EnchantmentHelper.getEnchantments(i).entrySet().stream())
                .filter(e -> e.getKey() instanceof BaseEnchantment && e.getValue() != null && e.getValue() > 0)
                .forEach(e -> map.compute((BaseEnchantment) e.getKey(), (k, v) -> {
                    v = v == null ? new Tuple<>(0, 0) : v;

                    int level = e.getValue();
                    // total level
                    v.setA(v.getA() + level);

                    // max level
                    if (v.getB() < level) {
                        v.setB(level);
                    }
                    return v;
                }));
        return map;
    }

    static class KV<K, V> {
        private K k;
        private V v;

        private KV(K k, V v) {
            this.k = k;
            this.v = v;
        }

        public static <K, V> KV<K, V> of(final K first, final V second) {
            return new KV<>(first, second);
        }

        public K k() {
            return this.k;
        }

        public V v() {
            return this.v;
        }

        public void k(K k) {
            this.k = k;
        }

        public void v(V v) {
            this.v = v;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            KV<?, ?> kv = (KV<?, ?>) o;

            if (!Objects.equals(k, kv.k)) return false;
            return Objects.equals(v, kv.v);
        }

        @Override
        public int hashCode() {
            int result = k != null ? k.hashCode() : 0;
            result = 31 * result + (v != null ? v.hashCode() : 0);
            return result;
        }
    }

    public static Optional<Enchantment> rand(Enchantment.Rarity rarity, Random random) {
        List<Enchantment> list = Registry.ENCHANTMENT.stream()
                .filter(e -> (rarity == null || e.getRarity() == rarity) && e.isDiscoverable())
                .collect(Collectors.toList());

        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(random.nextInt(list.size())));
    }
}
