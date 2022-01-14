package com.doo.xenchant.util;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.attribute.LimitTimeModifier;
import com.doo.xenchant.enchantment.*;
import com.doo.xenchant.enchantment.halo.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.tool.attribute.v1.ToolManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SwordItem;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 附魔工具
 */
@SuppressWarnings("all")
public class EnchantUtil {

    private EnchantUtil() {
    }

    /**
     * 所有盔甲
     */
    public static final EquipmentSlot[] ALL_ARMOR = new EquipmentSlot[]{
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    /**
     * 吸血记录
     */
    private static final Map<Integer, Integer> SUCK_LOG = new HashMap<>();

    /**
     * 攻击记录
     */
    private static final Map<Integer, Integer> WEAKNESS_LOG = new HashMap<>();

    /**
     * 线程池
     */
    public static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(3);

    /**
     * 可翻译文本
     */
    public static final MutableText LOOT_TEXT =
            new TranslatableText("enchantment.x_enchant.chat.more_loot")
                    .setStyle(Style.EMPTY.withColor(Formatting.YELLOW));
    public static final MutableText MORE_LOOT_TEXT =
            new TranslatableText("enchantment.x_enchant.chat.more_more_loot")
                    .setStyle(Style.EMPTY.withColor(Formatting.RED));

    /**
     * 注册所有附魔及事件
     */
    public static void registerAll() {
        // normal enchantments
        Stream.of(AutoFish.class, SuckBlood.class,
                        Weakness.class, Rebirth.class,
                        MoreLoot.class, HitRateUp.class,
                        QuickShoot.class, MagicImmune.class)
                .forEach(c -> BaseEnchantment.get(c).register());

        // Halo enchantments
        Stream.of(SlownessHalo.class, MaxHPUpHalo.class,
                        RegenerationHalo.class, ThunderHalo.class,
                        LuckHalo.class, AttackSpeedUpHalo.class)
                .forEach(c -> BaseEnchantment.get(c).register());

        // server listener
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
                ServerPlayerEntity player = handler.player;
                if (player != null) {
                    // remove log
                    SUCK_LOG.remove(player.getId());
                    WEAKNESS_LOG.remove(player.getId());
                }
            });
        }
    }

    public static void suckBlood(LivingEntity attacker, float amount, Box box) {
        ItemStack itemStack = attacker.getStackInHand(Hand.MAIN_HAND);
        // no level
        int level = BaseEnchantment.get(SuckBlood.class).level(itemStack);
        if (level < 1) {
            return;
        }

        // log
        int id = attacker.getId();
        int age = attacker.getLastAttackTime();
        if (SUCK_LOG.put(id, age) == age) {
            return;
        }

        // attack multi target and has sweep
        long count = itemStack.getItem() instanceof SwordItem ?
                attacker.world.getNonSpectatingEntities(LivingEntity.class, box).stream().filter(l -> canAttacked(attacker, l)).count() : 0;

        // suck scale
        boolean moreWithSweep = count > 1 && EnchantmentHelper.getLevel(Enchantments.SWEEPING, itemStack) > 0;
        float scale = level * (1F + (moreWithSweep ? Math.min(0.1F * count, 0.5F) : 0F));
        attacker.heal(scale * amount / 10);
    }

    /**
     * 能否攻击
     * <p>
     * (判断参考如下)
     * see PlayerEntity.attack(Entity target)
     *
     * @param attacker     玩家
     * @param livingEntity 存活对象
     * @return true or false
     */
    private static boolean canAttacked(LivingEntity attacker, LivingEntity livingEntity) {
        // 排除当前对象
        return livingEntity != attacker
                // 距离小于9
                && attacker.squaredDistanceTo(livingEntity) < 9.0
                // 排除队友
                && !attacker.isTeammate(livingEntity)
                // 可攻击
                && !(livingEntity instanceof ArmorStandEntity && ((ArmorStandEntity) livingEntity).isMarker());
    }

    /**
     * 弱点攻击
     *
     * @param attack 玩家
     * @param amount 伤害量
     */
    public static float weakness(LivingEntity attack, float amount) {
        ItemStack itemStack = attack.getStackInHand(Hand.MAIN_HAND);
        // no sword
        if (!(itemStack.getItem() instanceof SwordItem || itemStack.getItem() instanceof RangedWeaponItem)) {
            return amount;
        }

        // no level
        int level = BaseEnchantment.get(Weakness.class).level(itemStack);
        if (level < 1) {
            return amount;
        }

        // log
        int id = attack.getId();
        int age = attack.getLastAttackTime();
        if (WEAKNESS_LOG.put(id, age) == age) {
            return amount;
        }

        // random number
        return attack.getRandom().nextInt(100) < 5 * level ? amount * 3 : amount;
    }

    /**
     * 重生
     * <p>
     * (判断参考如下)
     * see net.minecraft.entity.LivingEntity#tryUseTotem(net.minecraft.entity.damage.DamageSource)
     *
     * @param player 玩家
     */
    public static void rebirth(LivingEntity player) {
        if (!isServerPlayer(player)) {
            return;
        }

        for (ItemStack armor : player.getArmorItems()) {
            if (BaseEnchantment.get(Rebirth.class).level(armor) > 0) {
                player.setHealth(player.getMaxHealth());
                player.clearStatusEffects();
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 500, 4));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 500, 4));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 500, 4));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 500, 2));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 500, 2));
                player.world.sendEntityStatus(player, (byte) 35);

                armor.getEnchantments().removeIf(tag -> BaseEnchantment.get((NbtCompound) tag) != null);
                return;
            }
        }
    }

    /**
     * 更多战利品
     *
     * @param context 上下文
     * @param rolls   基数
     */
    public static int loot(LootContext context) {
        ItemStack itemStack = context.get(LootContextParameters.TOOL);
        if (itemStack == null) {
            Entity entity = context.get(LootContextParameters.KILLER_ENTITY);
            if (entity instanceof ServerPlayerEntity) {
                itemStack = ((ServerPlayerEntity) entity).getMainHandStack();
            }
        }
        if (itemStack == null || itemStack.isEmpty()) {
            return 1;
        }

        // no effect on
        BlockState block = context.get(LootContextParameters.BLOCK_STATE);
        if (!ToolManager.handleIsEffectiveOn(block, itemStack, null)) {
            return 1;
        }

        // no level
        int level = BaseEnchantment.get(MoreLoot.class).level(itemStack);
        if (level < 1) {
            return 1;
        }

        // 20%
        int ran = context.getRandom().nextInt(100);
        if (ran >= Enchant.option.moreLootRate - 1) {
            return 1;
        }
        // 5%
        if (ran < Enchant.option.moreMoreLootRate - 1) {
            level *= Enchant.option.moreMoreLootMultiplier;
            sendMessage(MORE_LOOT_TEXT);
        } else {
            sendMessage(LOOT_TEXT);
        }

        return level + 1;
    }

    /**
     * 聊天框发送信息
     *
     * @param text text
     */
    private static void sendMessage(Text text) {
        if (Enchant.option.chatTips) {
            Enchant.MC.inGameHud.getChatHud().addMessage(text);
        }
    }

    /**
     * 获取命中等级
     *
     * @param itemStack 工具
     * @return level
     */
    public static int hitRateUp(ItemStack itemStack) {
        return BaseEnchantment.get(HitRateUp.class).level(itemStack);
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
     * @param effect 效果
     * @return 是否需要免疫
     */
    public static boolean magicImmune(ServerPlayerEntity player, StatusEffectInstance effect) {
        if (player == null && StatusEffectCategory.HARMFUL.equals(effect.getEffectType().getCategory())) {
            return false;
        }

        return BaseEnchantment.get(MagicImmune.class).level(player.getEquippedStack(EquipmentSlot.CHEST)) > 0;
    }

    /**
     * 触发光环
     *
     * @param uuid  玩家id
     * @param armor 装备栏
     */
    public static void halo(LivingEntity player) {
        if (!isServerPlayer(player)) {
            return;
        }

        Iterable<ItemStack> armor = player.getArmorItems();
        Map<HaloEnchantment, Integer> haloMap = new HashMap<>();
        armor.forEach(i ->
                i.getEnchantments().forEach(tag -> {
                    NbtCompound t = (NbtCompound) tag;
                    BaseEnchantment e = BaseEnchantment.get(EnchantmentHelper.getIdFromNbt(t));
                    if (e instanceof HaloEnchantment) {
                        haloMap.put((HaloEnchantment) e, haloMap.getOrDefault(e, 0) + 1);
                    }
                })
        );
        haloMap.keySet().removeIf(k -> haloMap.getOrDefault(k, -1) < 4);
        if (haloMap.isEmpty()) {
            return;
        }

        Map<Boolean, List<LivingEntity>> entities =
                player.world.getNonSpectatingEntities(LivingEntity.class, player.getBoundingBox().expand(Enchant.option.haloRange))
                        .stream().collect(Collectors.groupingBy(e -> e == player || e.isTeammate(player)));
        haloMap.forEach((k, v) -> k.tickHalo((PlayerEntity) player, v, entities::get));
    }

    /**
     * 移除过期的属性操作
     *
     * @param attributes 属性
     */
    public static void removedDirtyHalo(AttributeContainer attributes) {
        HaloEnchantment.ATTRIBUTES.forEach(a -> {
            EntityAttributeInstance instance = attributes.getCustomInstance(a);
            if (instance != null) {
                instance.getModifiers().forEach(m -> {
                    if ((m instanceof LimitTimeModifier && ((LimitTimeModifier) m).isExpire())) {
                        instance.removeModifier(m);
                    }
                });
            }
        });
    }

    public static boolean isServerPlayer(LivingEntity livingEntity) {
        return livingEntity instanceof ServerPlayerEntity;
    }
}
