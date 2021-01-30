package com.doo.xenchant.util;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.attribute.LimitTimeModifier;
import com.doo.xenchant.config.Config;
import com.doo.xenchant.enchantment.*;
import com.doo.xenchant.enchantment.halo.*;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 附魔工具
 */
public class EnchantUtil {

    /**
     * 所有盔甲
     */
    public static final EquipmentSlot[] ALL_ARMOR =
            new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    /**
     * 附魔表
     */
    public static final Map<String, BaseEnchantment> ENCHANTMENT_MAP = new HashMap<>();
    /**
     * 吸血记录
     */
    public static final Map<Integer, Integer> SUCK_FLAG_MAP = new HashMap<>();

    /**
     * 攻击记录
     */
    public static final Map<Integer, Integer> WEAKNESS_FLAG_MAP = new HashMap<>();

    /**
     * 战利品源数据记录
     */
    private static final Map<LootTableRange, Integer> ROLLS_MAP = new HashMap<>();

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
     * 注册所有附魔
     */
    public static void registerAll() {
        if (!ENCHANTMENT_MAP.isEmpty()) {
            return;
        }
        Stream.of(
                // 附魔
                new AutoFish(), new SuckBlood(), new Weakness(), new Rebirth(),
                new MoreLoot(), new HitRateUp(), new QuickShoot(), new MagicImmune(),
                // 光环
                new SlownessHalo(), new MaxHPUpHalo(), new RegenerationHalo(),
                new ThunderHalo(), new LuckHalo(), new AttackSpeedUpHalo()
        ).forEach(e -> ENCHANTMENT_MAP.put(e.getId().toString(), e));
    }

    /**
     * 获取附魔级别
     *
     * @param name      附魔名称
     * @param itemStack 物品
     * @return 等级
     */
    private static int getLevel(String name, ItemStack itemStack) {
        return EnchantmentHelper.getLevel(ENCHANTMENT_MAP.get(name), itemStack);
    }

    /**
     * 自动钓鱼
     *
     * @param user 玩家
     */
    public static void autoFish(ServerPlayerEntity user) {
        // 没有使用
        Hand hand = user.getActiveHand();
        if (hand == null) {
            return;
        }
        // 不为空
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            return;
        }
        // 仅鱼竿有效
        if (!(itemStack.getItem() instanceof FishingRodItem)) {
            return;
        }
        // 附魔判断
        if (getLevel(AutoFish.NAME, itemStack) < 1) {
            return;
        }
        // 50%概率 耐久 + 1
        int damage = itemStack.getDamage();
        if (damage > 0 && user.getRandom().nextBoolean()) {
            itemStack.setDamage(damage - 1);
        }
        // 收杆
        if (user.fishHook != null) {
            itemStack.use(user.world, user, hand);
        }
        // 自动触发钓鱼事件
        EXECUTOR.schedule(() -> {
            if (itemStack.isEmpty() || !itemStack.equals(user.getStackInHand(hand)) || user.fishHook != null) {
                return;
            }
            itemStack.use(user.world, user, hand);
        }, 200, TimeUnit.MILLISECONDS);
    }

    /**
     * 吸血
     *
     * @param player 玩家
     * @param amount 伤害量
     * @param box    攻击范围
     */
    public static void suckBlood(ServerPlayerEntity player, float amount, Box box) {
        ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);
        // 如果没有附魔
        int level = getLevel(SuckBlood.NAME, itemStack);
        if (level < 1) {
            return;
        }
        // 是否已经＋过了
        int id = player.getEntityId();
        int age = player.getLastAttackTime();
        if (SUCK_FLAG_MAP.getOrDefault(id, -1) >= age) {
            return;
        }
        // 记录
        SUCK_FLAG_MAP.put(id, age);
        // 如果是剑则判断是否攻击了多个目标
        long count = itemStack.getItem() instanceof SwordItem ?
                player.world.getNonSpectatingEntities(LivingEntity.class, box)
                        .stream().filter(l -> canAttacked(player, l)).count() : 0;
        // 吸血比例
        float scale = level * (0.1F + (count > 1 && getLevel("sweeping", itemStack) > 0 ? 0.05F : 0.00F));
        player.heal(scale * amount);
    }

    /**
     * 能否攻击
     * <p>
     * (判断参考如下)
     * see PlayerEntity.attack(Entity target)
     *
     * @param player       玩家
     * @param livingEntity 存活对象
     * @return true or false
     */
    private static boolean canAttacked(ServerPlayerEntity player, LivingEntity livingEntity) {
        // 排除当前对象
        return livingEntity != player
                // 距离小于9
                && player.squaredDistanceTo(livingEntity) < 9.0
                // 排除队友
                && !player.isTeammate(livingEntity)
                // 可攻击
                && !(livingEntity instanceof ArmorStandEntity && ((ArmorStandEntity) livingEntity).isMarker());
    }

    /**
     * 弱点攻击
     *
     * @param player 玩家
     * @param amount 伤害量
     */
    public static float weakness(ServerPlayerEntity player, float amount) {
        ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);
        // 如果不是剑且没有附魔
        int level;
        if (!(itemStack.getItem() instanceof SwordItem) || (level = getLevel(Weakness.NAME, itemStack)) < 1) {
            return amount;
        }
        // 已经判断过了
        int id = player.getEntityId();
        int age = player.getLastAttackTime();
        if (WEAKNESS_FLAG_MAP.getOrDefault(id, -1) >= age) {
            return amount;
        }
        // 记录
        WEAKNESS_FLAG_MAP.put(id, age);
        // 根据等级判断是否造成弱点攻击
        return player.getRandom().nextInt(100) < 5 * level ? amount * 3 : amount;
    }

    /**
     * 重生
     * <p>
     * (判断参考如下)
     * see net.minecraft.entity.LivingEntity#tryUseTotem(net.minecraft.entity.damage.DamageSource)
     *
     * @param player 玩家
     */
    public static void rebirth(ServerPlayerEntity player) {
        for (ItemStack armor : player.getArmorItems()) {
            if (getLevel(Rebirth.NAME, armor) > 0) {
                player.setHealth(player.getMaxHealth());
                player.clearStatusEffects();
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 500, 4));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 500, 4));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 500, 4));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 500, 2));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 500, 2));
                player.world.sendEntityStatus(player, (byte) 35);
                armor.getEnchantments().removeIf(tag ->
                        (ENCHANTMENT_MAP.get(Rebirth.NAME).getId().toString().equals(((CompoundTag) tag).getString("id"))));
                return;
            }
        }
    }

    /**
     * 获取服务端玩家
     *
     * @param uuid uuid
     */
    public static ServerPlayerEntity getServerPlayer(UUID uuid) {
        MinecraftServer server = Enchant.MC.getServer();
        if (server == null) {
            return null;
        }
        return server.getPlayerManager().getPlayer(uuid);
    }

    /**
     * 更多战利品
     *
     * @param context 上下文
     * @param rolls   基数
     */
    public static void moreLoot(LootContext context, LootTableRange rolls) {
        ItemStack itemStack = context.get(LootContextParameters.TOOL);
        if (itemStack == null) {
            Entity entity = context.get(LootContextParameters.KILLER_ENTITY);
            if (entity instanceof ServerPlayerEntity) {
                itemStack = ((ServerPlayerEntity) entity).getMainHandStack();
            }
        }
        if (itemStack == null || itemStack.isEmpty()) {
            return;
        }
        // 没有附魔
        int level = getLevel(MoreLoot.NAME, itemStack);
        if (level < 1) {
            return;
        }
        // 不是无效的
        BlockState block = context.get(LootContextParameters.BLOCK_STATE);
        if (block != null && itemStack.getMiningSpeedMultiplier(block) <= 1) {
            return;
        }
        // 20%的几率
        int ran = context.getRandom().nextInt(100);
        if (ran >= 20) {
            return;
        }
        //  5%
        if (ran < 5) {
            level *= 10;
            sendMessage(MORE_LOOT_TEXT);
        } else {
            sendMessage(LOOT_TEXT);
        }
        try {
            level += 1;
            Field field = ConstantLootTableRange.class.getDeclaredField("value");
            field.setAccessible(true);
            int value = field.getInt(rolls);
            field.set(rolls, level * value);
            ROLLS_MAP.put(rolls, value);
        } catch (Exception e) {
            Config.LOGGER.log(Level.WARN, "value设置失败", e);
        }
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
     * 重置战利品生成基数
     *
     * @param rolls 基数
     */
    public static void resetLoot(LootTableRange rolls) {
        if (!ROLLS_MAP.containsKey(rolls)) {
            return;
        }
        try {
            Field field = ConstantLootTableRange.class.getDeclaredField("value");
            field.setAccessible(true);
            field.set(rolls, ROLLS_MAP.remove(rolls));
        } catch (Exception e) {
            Config.LOGGER.log(Level.WARN, "value重置失败", e);
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
    public static Entity hitRateUp(ServerPlayerEntity player, ItemStack itemStack, World world, Vec3d pos, Box box) {
        int level = getLevel(HitRateUp.NAME, itemStack);
        if (level < 1) {
            return null;
        }
        List<LivingEntity> entities =
                world.getNonSpectatingEntities(LivingEntity.class, box.expand(level));
        entities.removeIf(e -> e == player || e.isTeammate(player));
        if (entities.isEmpty()) {
            return null;
        }
        return entities.stream()
                .filter(e -> e.squaredDistanceTo(pos) <= level)
                .min(Comparator.comparingDouble(p -> p.squaredDistanceTo(pos)))
                .orElse(null);
    }

    /**
     * 快速射击
     *
     * @param itemStack 物品栈
     * @return level tick
     */
    public static int quickShooting(ItemStack itemStack) {
        return getLevel(QuickShoot.NAME, itemStack);
    }

    /**
     * 魔免判断
     *
     * @param uuid   玩家id
     * @param effect 效果
     * @return 是否需要免疫
     */
    public static boolean magicImmune(UUID uuid, StatusEffectInstance effect) {
        ServerPlayerEntity player;
        return (player = getServerPlayer(uuid)) != null
                && getLevel(MagicImmune.NAME, player.getEquippedStack(EquipmentSlot.CHEST)) > 0
                && StatusEffectType.HARMFUL.equals(effect.getEffectType().getType());
    }

    /**
     * 触发光环
     *
     * @param uuid  玩家id
     * @param armor 装备栏
     */
    public static void halo(UUID uuid, Iterable<ItemStack> armor) {
        PlayerEntity player;
        if ((player = getServerPlayer(uuid)) == null) {
            return;
        }
        Map<HaloEnchantment, Integer> haloMap = new HashMap<>();
        armor.forEach(i ->
                i.getEnchantments().forEach(tag -> {
                    CompoundTag t = (CompoundTag) tag;
                    BaseEnchantment e = ENCHANTMENT_MAP.get(t.getString("id"));
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
                player.world.getNonSpectatingEntities(LivingEntity.class, player.getBoundingBox().expand(9))
                        .stream().collect(Collectors.groupingBy(e -> e == player || e.isTeammate(player)));
        haloMap.forEach((k, v) -> k.tickHalo(player, v, entities.get(true), entities.get(false)));
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
}
