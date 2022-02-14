package com.doo.xenchant.util;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.enchantment.*;
import com.doo.xenchant.enchantment.halo.AttrHalo;
import com.doo.xenchant.enchantment.halo.EffectHalo;
import com.doo.xenchant.enchantment.halo.HeightAdvantageHalo;
import com.doo.xenchant.enchantment.halo.ThunderHalo;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ToolItem;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 附魔工具
 */
@SuppressWarnings("all")
public class EnchantUtil {

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
        Stream.of(AutoFish.class, SuckBlood.class, Weakness.class, Rebirth.class,
                        MoreLoot.class, HitRateUp.class, QuickShoot.class, MagicImmune.class,
                        Librarian.class, IncDamage.class, Climber.class, Smart.class,
                        KingKongLegs.class, Diffusion.class, Elasticity.class)
                .forEach(c -> BaseEnchantment.get(c).register());

        // Halo enchantments
        Stream.of(ThunderHalo.class, HeightAdvantageHalo.class).forEach(c -> BaseEnchantment.get(c).register());

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

    private static void registAttr() {
        // Status effect halo must regist after all mod loaded
        // need filter(s -> Identifier.isValid(s.getTranslationKey()))
        // if not exsits
        Registry.ATTRIBUTE.getEntries().stream()
                .filter(e -> Enchant.option.attributes.contains(e.getValue().getTranslationKey()))
                .forEach(e -> new AttrHalo(e.getValue()));
    }

    private static void registEffect() {
        // Attribute halo must regist after all mod loaded
        // need filter(s -> Identifier.isValid(s.getTranslationKey()))
        // if not exsits
        Registry.STATUS_EFFECT.stream()
                .filter(e -> e != null && Identifier.isValid(e.getTranslationKey()) && !Enchant.option.disabledEffect.contains(e.getTranslationKey()))
                .forEach(EffectHalo::new);
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
     * 魔免判断
     *
     * @param uuid   玩家id
     * @param living
     * @param effect 效果
     * @return 是否需要免疫
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
        StreamSupport.stream(living.getItemsEquipped().spliterator(), false).forEach(stack -> {
            if (stack.getEnchantments().isEmpty()) {
                return;
            }

            stack.getEnchantments().stream()
                    .filter(n -> BaseEnchantment.isBase(id(n)) && lvl(n) > 0)
                    .forEach(n -> {
                        // old enchantment is null
                        Optional.ofNullable((BaseEnchantment) BaseEnchantment.get(id(n))).ifPresent(e -> e.tryTrigger(living, stack, lvl(n)));
                    });
        });
    }

    public static String id(NbtElement n) {
        return ((NbtCompound) n).getString("id");
    }

    public static int lvl(NbtElement n) {
        return ((NbtCompound) n).getInt("lvl");
    }

    public static boolean hasAttackDamage(ItemStack stack) {
        return !stack.isEmpty() &&
                (stack.getItem() instanceof RangedWeaponItem || stack.getItem() instanceof ToolItem ||
                        !stack.getItem().getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE).isEmpty() ||
                        !stack.getItem().getAttributeModifiers(EquipmentSlot.OFFHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE).isEmpty());
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
     * @param function
     * @param stack
     * @return
     * @see EnchantmentHelper#forEachEnchantment(net.minecraft.enchantment.EnchantmentHelper.Consumer, net.minecraft.item.ItemStack)
     */
    public static void forBaseEnchantment(BiConsumer<BaseEnchantment, Integer> consumer, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        NbtList nbtList = stack.getEnchantments();
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            Registry.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound))
                    // only BaseEnchantment
                    .filter(enchantment -> enchantment instanceof BaseEnchantment).ifPresent(enchantment -> consumer.accept((BaseEnchantment) enchantment, EnchantmentHelper.getLevelFromNbt(nbtCompound)));
        }
    }

    public static float additionDamage(LivingEntity attacker, LivingEntity target) {
        MutableFloat newAmount = new MutableFloat(0);
        Stream.of(attacker.getMainHandStack(), attacker.getOffHandStack()).forEach(stack -> {
            forBaseEnchantment((e, l) -> newAmount.add(e.getAdditionDamage(attacker, target, stack, l)), stack);
        });
        return newAmount.floatValue();
    }

    public static float multiTotalDamage(LivingEntity attacker, LivingEntity target) {
        MutableFloat newAmount = new MutableFloat(1);
        Stream.of(attacker.getMainHandStack(), attacker.getOffHandStack()).forEach(stack -> {
            forBaseEnchantment((e, l) -> newAmount.add(e.getMultiTotalDamage(attacker, target, stack, l)), stack);
        });
        return newAmount.floatValue();
    }

    public static float realAdditionDamage(LivingEntity attacker, LivingEntity target) {
        MutableFloat newAmount = new MutableFloat(0);
        Stream.of(attacker.getMainHandStack(), attacker.getOffHandStack()).forEach(stack -> {
            forBaseEnchantment((e, l) -> newAmount.add(e.getRealAdditionDamage(attacker, target, stack, l)), stack);
        });
        return newAmount.floatValue();
    }

    public static float realMultiTotalDamage(LivingEntity attacker, LivingEntity target) {
        MutableFloat newAmount = new MutableFloat(1);
        Stream.of(attacker.getMainHandStack(), attacker.getOffHandStack()).forEach(stack -> {
            forBaseEnchantment((e, l) -> newAmount.add(e.getRealMultiTotalDamage(attacker, target, stack, l)), stack);
        });
        return newAmount.floatValue();
    }

    public static void damageCallback(LivingEntity attacker, LivingEntity target, float amount) {
        Stream.of(attacker.getMainHandStack(), attacker.getOffHandStack()).forEach(stack -> {
            forBaseEnchantment((e, l) -> e.damageCallback(attacker, target, stack, l, amount), stack);
        });

        attacker.getArmorItems().forEach(stack -> {
            forBaseEnchantment((e, l) -> e.damageCallback(attacker, target, stack, l, amount), stack);
        });
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
        BiConsumer<BaseEnchantment, Integer> forEach = (e, l) ->
                Optional.ofNullable(e.lootSetter(killer, item, l, lootConsumer, context)).ifPresent(list::add);
        forBaseEnchantment(forEach, stack);

        if (list.isEmpty()) {
            return lootConsumer;
        }

        return lootConsumer.andThen(i -> list.stream().reduce((c1, c2) -> c1.andThen(c2)).get().apply(i));
    }
}
