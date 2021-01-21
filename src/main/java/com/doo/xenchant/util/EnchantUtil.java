package com.doo.xenchant.util;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.config.Config;
import com.doo.xenchant.enchantment.*;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
            new TranslatableText("enchantment.xenchant.chat.more_loot").setStyle(Style.EMPTY.withColor(Formatting.YELLOW));
    public static final MutableText MORE_LOOT_TEXT =
            new TranslatableText("enchantment.xenchant.chat.more_more_loot").setStyle(Style.EMPTY.withColor(Formatting.RED));

    /**
     * 注册所有附魔
     */
    public static void registerAll() {
        if (!ENCHANTMENT_MAP.isEmpty()) {
            return;
        }
        ENCHANTMENT_MAP.put(AutoFish.NAME, new AutoFish());
        ENCHANTMENT_MAP.put(SuckBlood.NAME, new SuckBlood());
        ENCHANTMENT_MAP.put(Weakness.NAME, new Weakness());
        ENCHANTMENT_MAP.put(Rebirth.NAME, new Rebirth());
        ENCHANTMENT_MAP.put(MoreLoot.NAME, new MoreLoot());
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
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 4));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 900, 4));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 900, 4));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 900, 2));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 900, 2));
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
        MinecraftServer server = MinecraftClient.getInstance().getServer();
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
        boolean chatTips = Enchant.option.chatTips;
        //  5%
        if (ran < 5) {
            level *= 5;
            if (chatTips) {
                Enchant.MC.inGameHud.getChatHud().addMessage(MORE_LOOT_TEXT);
            }
        } else {
            if (chatTips) {
                Enchant.MC.inGameHud.getChatHud().addMessage(LOOT_TEXT);
            }
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
}
