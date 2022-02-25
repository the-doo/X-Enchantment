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
import com.doo.xenchant.enchantment.special.RemoveCursed;
import com.doo.xenchant.enchantment.trinkets.Trinkets;
import com.doo.xenchant.events.EntityArmorApi;
import com.doo.xenchant.events.EntityDamageApi;
import com.doo.xenchant.events.LootApi;
import com.google.common.collect.Maps;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 附魔工具
 */
public class EnchantUtil {

    public static boolean hasTrinkets = false;

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
                MoreLoot.class, HitRateUp.class, QuickShoot.class, MagicImmune.class,
                Librarian.class, IncDamage.class, Climber.class, Smart.class,
                KingKongLegs.class, Diffusion.class, Elasticity.class,
                NightBreak.class, BrokenDawn.class, Timor.class);
        processStream(stream);

        // cursed enchantments
        stream = Stream.of(Regicide.class, Thin.class, DownDamage.class, DownArmor.class);
        processStream(stream);

        // Special enchantments
        if (Enchant.option.special) {
            stream = Stream.of(RemoveCursed.class, HealthConverter.class);
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
                .map(BaseEnchantment::get)
                .sorted(Comparator.comparing(e -> ((BaseEnchantment) e).getRarity().getWeight()).reversed())
                .forEach(BaseEnchantment::register);
    }

    private static void regisAttr() {
        // Status effect halo must regis after all mod loaded
        // need filter(s -> Identifier.isValid(s.getTranslationKey()))
        // if not exists
        Registry.ATTRIBUTE.getEntries().stream()
                .filter(e -> Enchant.option.attributes.contains(e.getValue().getTranslationKey()))
                .map(e -> new AttrHalo(e.getValue()))
                .sorted(Comparator.comparing(e -> ((BaseEnchantment) e).getRarity().getWeight()).reversed())
                .forEach(BaseEnchantment::register);
    }

    private static void regisEffect() {
        // Attribute halo must regis after all mod loaded
        // need filter(s -> Identifier.isValid(s.getTranslationKey()))
        // if not exists
        Registry.STATUS_EFFECT.stream()
                .filter(e -> e != null && Identifier.isValid(e.getTranslationKey()) && !Enchant.option.disabledEffect.contains(e.getTranslationKey()))
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

    /**
     * 聊天框发送信息
     *
     * @param senderName sender
     * @param text       text
     */
    public static void sendMessage(ServerPlayerEntity player, Text senderName, Text text) {
        if (Enchant.option.chatTips) {
            player.networkHandler.onChatMessage(new ChatMessageC2SPacket(senderName.shallowCopy().formatted(Formatting.GOLD).append(": ").append(text).getString()));
        }
    }

    public static ItemStack getHandStack(LivingEntity entity, Class<? extends Item> type) {
        return getHandStack(entity, type, null);
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

    /**
     * 命中
     *
     * @param player    玩家
     * @param itemStack 工具
     * @param world     世界
     * @param pos       位置
     * @param box       碰撞体积
     * @return Entity 命中实体 or null
     */
    public static Entity hitRateUp(Entity player, ItemStack itemStack, World world, Vec3d pos, Box box) {
        int level = BaseEnchantment.get(HitRateUp.class).level(itemStack);
        if (level < 1) {
            return null;
        }
        return world.getOtherEntities(player, box.expand(level), e -> e instanceof LivingEntity).stream().filter(e -> !e.isTeammate(player) && e.squaredDistanceTo(pos) <= level).findFirst().orElse(null);
    }

    /**
     * 快速射击
     *
     * @param itemStack 物品栈
     * @return level tick
     */
    public static int quickShooting(ItemStack itemStack) {
        return BaseEnchantment.get(QuickShoot.class).level(itemStack);
    }

    /**
     * return: is effect on
     */
    public static boolean magicImmune(LivingEntity living, StatusEffectInstance effect) {
        if (living == null || StatusEffectCategory.HARMFUL != effect.getEffectType().getCategory()) {
            return false;
        }

        return BaseEnchantment.get(MagicImmune.class).level(living.getEquippedStack(EquipmentSlot.CHEST)) > 0;
    }

    /**
     * elasticity
     */
    public static int elasticity(ItemStack itemStack) {
        return BaseEnchantment.get(Elasticity.class).level(itemStack);
    }

    public static Consumer<ItemStack> lootConsumer(Consumer<ItemStack> lootConsumer, LootContext context) {
        // default is tool loot
        ItemStack stack = context.get(LootContextParameters.TOOL);
        Entity entity = Optional.ofNullable(context.get(LootContextParameters.KILLER_ENTITY))
                .orElse(context.get(LootContextParameters.THIS_ENTITY));

        // if is attack loot, try to get on entity
        if (stack == null && entity instanceof LivingEntity) {
            stack = ((LivingEntity) entity).getMainHandStack();
        }

        // if is rod loot, try to get owner
        if (entity instanceof FishingBobberEntity) {
            entity = ((FishingBobberEntity) entity).getOwner();
        }

        if (stack == null || stack.isEmpty() || !(entity instanceof LivingEntity)) {
            return lootConsumer;
        }

        Function<ItemStack, ItemStack> handle = LootApi.HANDLER.invoker().handle((LivingEntity) entity, stack, lootConsumer, context);
        if (handle == null) {
            return lootConsumer;
        }

        return lootConsumer.andThen(handle::apply);
    }

    public static float damage(float amount, DamageSource source, LivingEntity target) {
        Entity entity = source.getAttacker();
        if (entity instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) entity;
            Map<BaseEnchantment, Integer> map = EnchantUtil.mergeOf(attacker);
            // is addition damage
            amount += Math.max(0, EntityDamageApi.ADD.invoker().get(attacker, target, map));
            if (amount <= 0) {
                return 0;
            }

            amount *= (1 + EntityDamageApi.MULTIPLIER.invoker().get(attacker, target, map));
            if (amount <= 0) {
                return 0;
            }
        }
        return amount;
    }

    public static float realDamage(float amount, DamageSource source, LivingEntity target) {
        Entity entity = source.getAttacker();
        if (entity instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) entity;
            Map<BaseEnchantment, Integer> map = EnchantUtil.mergeOf(attacker);
            // is addition damage
            amount += Math.max(0, EntityDamageApi.REAL_ADD.invoker().get(attacker, target, map));
            if (amount <= 0) {
                return 0;
            }

            amount *= (1 + EntityDamageApi.REAL_MULTIPLIER.invoker().get(attacker, target, map));
            if (amount <= 0) {
                return 0;
            }
        }
        return amount;
    }

    public static double armor(double base, LivingEntity living) {
        Map<BaseEnchantment, Integer> map = EnchantUtil.mergeOf(living);
        // is addition damage
        base += Math.max(0, EntityArmorApi.ADD.invoker().get(living, base, map));
        if (base <= 0) {
            return 0;
        }

        base *= (1 + EntityArmorApi.MULTIPLIER.invoker().get(living, base, map));
        if (base <= 0) {
            return 0;
        }
        return base;
    }

    /**
     * Get enchantments on merge
     */
    public static Map<BaseEnchantment, Integer> mergeOf(LivingEntity living) {
        List<ItemStack> temps = new ArrayList<>();
        living.getItemsEquipped().forEach(temps::add);
        if (hasTrinkets) {
            TrinketsApi.getTrinketComponent(living).ifPresent(c -> c.getAllEquipped().forEach(p -> temps.add(p.getRight())));
        }


        Map<BaseEnchantment, Integer> map = Maps.newHashMap();
        temps.stream().filter(ItemStack::hasEnchantments)
                .flatMap(i -> EnchantmentHelper.get(i).entrySet().stream())
                .forEach(e -> {
                    if (e.getKey() instanceof BaseEnchantment && e.getValue() != null && e.getValue() > 0) {
                        map.compute((BaseEnchantment) e.getKey(), (k, v) -> Optional.ofNullable(v).orElse(0) + e.getValue());
                    }
                });
        return map;
    }
}
