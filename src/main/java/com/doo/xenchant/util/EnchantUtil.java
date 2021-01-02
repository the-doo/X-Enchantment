package com.doo.xenchant.util;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.enchantment.AutoFish;
import com.doo.xenchant.enchantment.SuckBlood;
import com.doo.xenchant.enchantment.Weakness;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 附魔工具
 */
public class EnchantUtil {

    /**
     * 附魔表
     */
    public static final Map<String, Enchantment> ENCHANTMENT_MAP = new HashMap<>();

    /**
     * 吸血记录
     */
    public static final Map<Integer, Integer> SUCK_FLAG_MAP = new HashMap<>();

    /**
     * 攻击记录
     */
    public static final Map<Integer, Integer> WEAKNESS_FLAG_MAP = new HashMap<>();

    /**
     * 线程池
     */
    public static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(3);

    /**
     * 注册所有附魔
     */
    public static void registerAll() {
        ENCHANTMENT_MAP.put(AutoFish.NAME, register(AutoFish.NAME, new AutoFish()));
        ENCHANTMENT_MAP.put(SuckBlood.NAME, register(SuckBlood.NAME, new SuckBlood()));
        ENCHANTMENT_MAP.put(Weakness.NAME, register(Weakness.NAME, new Weakness()));
    }

    /**
     * 注册附魔
     *
     * @param enchantment 附魔对象
     * @return 返回注册后的
     */
    public static Enchantment register(String name, Enchantment enchantment) {
        return Registry.register(Registry.ENCHANTMENT, new Identifier(Enchant.ID, name), enchantment);
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
        // 距离小于9
        return player.squaredDistanceTo(livingEntity) < 9.0
                // 排除当前对象
                && livingEntity != player
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
}
