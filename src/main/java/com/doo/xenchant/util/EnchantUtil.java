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
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
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
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
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

        // Halo enchantments
        if (Enchant.option.halo) {
            stream = Stream.of(ThunderHalo.class, HeightAdvantageHalo.class);
            processStream(stream);

            // regist to server
            ServerLifecycleEvents.SERVER_STARTED.register(server -> {
                registEffect();

                registAttr();
            });

            // regist to client
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
                    registEffect();

                    registAttr();
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

    private static void registAttr() {
        // Status effect halo must regist after all mod loaded
        // need filter(s -> Identifier.isValid(s.getTranslationKey()))
        // if not exsits
        Registry.ATTRIBUTE.getEntries().stream()
                .filter(e -> Enchant.option.attributes.contains(e.getValue().getTranslationKey()))
                .map(e -> new AttrHalo(e.getValue()))
                .sorted(Comparator.comparing(e -> ((BaseEnchantment) e).getRarity().getWeight()).reversed())
                .forEach(BaseEnchantment::register);
    }

    private static void registEffect() {
        // Attribute halo must regist after all mod loaded
        // need filter(s -> Identifier.isValid(s.getTranslationKey()))
        // if not exsits
        Registry.STATUS_EFFECT.stream()
                .filter(e -> e != null && Identifier.isValid(e.getTranslationKey()) && !Enchant.option.disabledEffect.contains(e.getTranslationKey()))
                .map(EffectHalo::new)
                .sorted(Comparator.comparing(e -> ((BaseEnchantment) e).getRarity().getWeight()).reversed())
                .forEach(BaseEnchantment::register);
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

    /**
     * living tick
     *
     * @param living living
     */
    public static void livingTick(LivingEntity living) {
        if (living.world.isClient()) {
            return;
        }

        // remove dirty arributes
        AttrHalo.removeDirty(living);

        // tick enchantment
        living.getItemsEquipped().forEach(stack -> {
            if (stack.isEmpty() || !stack.hasEnchantments()) {
                return;
            }

            forBaseEnchantment((e, l) -> e.tryTrigger(living, stack, l), stack);
        });

        // if has trinkets
        ifTrinket(stack -> {
            if (stack.isEmpty() || !stack.hasEnchantments()) {
                return;
            }

            forBaseEnchantment((e, l) -> e.tryTrigger(living, stack, l), stack);
        }, living);
    }

    public static ItemStack getHandStack(LivingEntity entity, Class<? extends Item> type) {
        if (entity != null) {
            ItemStack item = entity.getStackInHand(Hand.MAIN_HAND);
            if (!type.isInstance(item.getItem())) {
                item = entity.getStackInHand(Hand.OFF_HAND);
            }
            return !type.isInstance(item.getItem()) ? ItemStack.EMPTY : item;
        }
        return ItemStack.EMPTY;
    }

    /**
     * foreach
     *
     * @see EnchantmentHelper#forEachEnchantment(net.minecraft.enchantment.EnchantmentHelper.Consumer, net.minecraft.item.ItemStack)
     */
    public static void forBaseEnchantment(BiConsumer<BaseEnchantment, Integer> consumer, ItemStack stack) {
        if (stack.isEmpty() || !stack.hasEnchantments()) {
            return;
        }

        EnchantmentHelper.get(stack).forEach((e, l) -> {
            if (e instanceof BaseEnchantment && l > 0) {
                consumer.accept((BaseEnchantment) e, l);
            }
        });
    }

    public static float additionDamage(LivingEntity attacker, LivingEntity target) {
        MutableFloat newAmount = new MutableFloat(0);
        Stream.of(attacker.getMainHandStack(), attacker.getOffHandStack()).forEach(stack -> forBaseEnchantment((e, l) -> newAmount.add(e.getAdditionDamage(attacker, target, stack, l)), stack));

        // if it has trinkets
        ifTrinket(stack -> forBaseEnchantment((e, l) -> newAmount.add(e.getAdditionDamage(attacker, target, stack, l)), stack), attacker);
        return newAmount.floatValue();
    }

    public static float multiTotalDamage(LivingEntity attacker, LivingEntity target) {
        MutableFloat newAmount = new MutableFloat(1);
        Stream.of(attacker.getMainHandStack(), attacker.getOffHandStack()).forEach(stack -> forBaseEnchantment((e, l) -> newAmount.add(e.getMultiTotalDamage(attacker, target, stack, l)), stack));

        // if it has trinkets
        ifTrinket(stack -> forBaseEnchantment((e, l) -> newAmount.add(e.getMultiTotalDamage(attacker, target, stack, l)), stack), attacker);
        return newAmount.floatValue();
    }

    public static float realAdditionDamage(LivingEntity attacker, LivingEntity target) {
        MutableFloat newAmount = new MutableFloat(0);
        Stream.of(attacker.getMainHandStack(), attacker.getOffHandStack()).forEach(stack -> forBaseEnchantment((e, l) -> newAmount.add(e.getRealAdditionDamage(attacker, target, stack, l)), stack));

        // if it has trinkets
        ifTrinket(stack -> forBaseEnchantment((e, l) -> newAmount.add(e.getRealAdditionDamage(attacker, target, stack, l)), stack), attacker);
        return newAmount.floatValue();
    }

    public static float multiTotalArmor(LivingEntity living, double total) {
        MutableFloat newAmount = new MutableFloat(1);
        living.getArmorItems().forEach(stack -> forBaseEnchantment((e, l) -> newAmount.add(e.getMultiTotalArmor(living, total, stack, l)), stack));

        // if it has trinkets
        ifTrinket(stack -> forBaseEnchantment((e, l) -> newAmount.add(e.getMultiTotalArmor(living, total, stack, l)), stack), living);
        return newAmount.floatValue();
    }

    public static void damageCallback(LivingEntity attacker, LivingEntity target, float amount) {
        Stream.of(attacker.getMainHandStack(), attacker.getOffHandStack()).forEach(stack -> forBaseEnchantment((e, l) -> e.damageCallback(attacker, target, stack, l, amount), stack));

        attacker.getArmorItems().forEach(stack -> forBaseEnchantment((e, l) -> e.damageCallback(attacker, target, stack, l, amount), stack));

        ifTrinket(stack -> forBaseEnchantment((e, l) -> e.damageCallback(attacker, target, stack, l, amount), stack), attacker);
    }

    public static void ifTrinket(Consumer<ItemStack> consumer, LivingEntity living) {
        if (!hasTrinkets) {
            return;
        }

        TrinketsApi.getTrinketComponent(living).ifPresent(c -> c.getAllEquipped().forEach(p -> consumer.accept(p.getRight())));
    }

    public static Consumer<ItemStack> lootConsumer(Consumer<ItemStack> lootConsumer, LootContext context) {
        // defualt is tool loot
        ItemStack stack = context.get(LootContextParameters.TOOL);
        Entity entity = Optional.ofNullable(context.get(LootContextParameters.KILLER_ENTITY))
                .orElse(context.get(LootContextParameters.THIS_ENTITY));

        // if is attack loot, try get on entity
        if (stack == null && entity instanceof LivingEntity) {
            stack = ((LivingEntity) entity).getMainHandStack();
        }

        // if is rod loot, try get owner
        if (entity instanceof FishingBobberEntity) {
            entity = ((FishingBobberEntity) entity).getOwner();
        }

        if (stack == null || stack.isEmpty() || !(entity instanceof LivingEntity)) {
            return lootConsumer;
        }

        ItemStack item = stack;
        LivingEntity killer = (LivingEntity) entity;

        List<Function<ItemStack, ItemStack>> list = new ArrayList<>();
        BiConsumer<BaseEnchantment, Integer> forEach = (e, l) -> Optional.ofNullable(e.lootSetter(killer, item, l, lootConsumer, context)).ifPresent(list::add);
        forBaseEnchantment(forEach, stack);

        if (list.isEmpty()) {
            return lootConsumer;
        }

        Function<ItemStack, ItemStack> function = list.stream().reduce(Function::andThen).get();
        return lootConsumer.andThen(function::apply);
    }

    public static void itemUsedCallback(LivingEntity owner, ItemStack stack, float amount) {
        forBaseEnchantment((e, l) -> e.itemUsedCallback(owner, stack, l, amount), stack);
    }

    public static Map<Enchantment, Integer> useOnAnvil(Map<Enchantment, Integer> enchantments, ItemStack newOne) {
        Set<BaseEnchantment> set = enchantments.keySet().stream()
                .filter(e -> e instanceof BaseEnchantment && enchantments.get(e) > 0)
                .map(e -> (BaseEnchantment) e)
                .collect(Collectors.toSet());

        set.forEach(e -> e.onAnvil(enchantments, enchantments.get(e), newOne));
        return enchantments;
    }
}
