package com.doo.xenchant.util;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.attribute.LimitTimeModifier;
import com.doo.xenchant.config.Config;
import com.doo.xenchant.enchantment.*;
import com.doo.xenchant.enchantment.halo.*;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
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
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.nbt.NbtCompound;
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
import org.lwjgl.glfw.GLFW;

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
@SuppressWarnings("all")
public class EnchantUtil {

    private EnchantUtil() {
    }

    /**
     * MouseRightClick
     */
    private static final InputUtil.Key MOUSE_RIGHT_CLICK =
            InputUtil.Type.MOUSE.createFromCode(GLFW.GLFW_MOUSE_BUTTON_RIGHT);

    /**
     * 所有盔甲
     */
    public static final EquipmentSlot[] ALL_ARMOR =
            new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    /**
     * 吸血记录
     */
    private static final Map<Integer, Integer> SUCK_FLAG_MAP = new HashMap<>();

    /**
     * 攻击记录
     */
    private static final Map<Integer, Integer> WEAKNESS_FLAG_MAP = new HashMap<>();

    /**
     * 战利品源数据记录
     */
    private static final Map<LootNumberProvider, Float> ROLLS_MAP = new HashMap<>();

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
    }

    /**
     * 获取附魔级别
     *
     * @param clazz     附魔类
     * @param itemStack 物品
     * @return 等级
     */
    private static <T extends BaseEnchantment> int level(Class<T> clazz, ItemStack itemStack) {
        return EnchantmentHelper.getLevel(BaseEnchantment.get(clazz), itemStack);
    }

    /**
     * 自动钓鱼
     */
    public static void autoFish(World world, Box box) {
        if (world == null) {
            return;
        }
        List<FishingBobberEntity> list = world.getNonSpectatingEntities(FishingBobberEntity.class, box.expand(3));
        if (list == null || list.isEmpty()) {
            return;
        }
        FishingBobberEntity bob = list.get(0);
        Entity owner = bob.getOwner();
        if (!(owner instanceof PlayerEntity) || owner != MinecraftClient.getInstance().player) {
            return;
        }
        PlayerEntity player = (PlayerEntity) owner;
        // 没有使用
        Hand hand = player.getActiveHand();
        if (hand == null) {
            return;
        }
        // 不为空
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            return;
        }
        // 仅鱼竿有效
        if (!(itemStack.getItem() instanceof FishingRodItem)) {
            return;
        }
        // 附魔判断
        if (level(AutoFish.class, itemStack) < 1) {
            return;
        }
        // 50%概率 耐久 + 1
        int damage = itemStack.getDamage();
        if (player.getRandom().nextBoolean()) {
            // -1 mean rollback
            NetworkUtil.incItemStackDamage(damage, -1, slot(player, itemStack), itemStack);
        }
        // 点击右键收杆
        KeyBinding.onKeyPressed(MOUSE_RIGHT_CLICK);
        // 点击右键钓鱼 --- 300ms延迟
        EXECUTOR.schedule(() -> {
            if (itemStack.isEmpty() || !itemStack.equals(player.getStackInHand(hand)) || player.fishHook != null) {
                return;
            }
            KeyBinding.onKeyPressed(MOUSE_RIGHT_CLICK);
        }, 300, TimeUnit.MILLISECONDS);
    }


    public static int slot(PlayerEntity player, ItemStack stack) {
        int slot = -1;
        for (ItemStack itemStack : player.getInventory().main) {
            slot++;
            if (itemStack == stack) {
                return slot;
            }
        }
        for (ItemStack itemStack : player.getInventory().armor) {
            slot++;
            if (itemStack == stack) {
                return slot;
            }
        }
        for (ItemStack itemStack : player.getInventory().offHand) {
            slot++;
            if (itemStack == stack) {
                return slot;
            }
        }
        return slot;
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
        int level = level(SuckBlood.class, itemStack);
        if (level < 1) {
            return;
        }
        // 是否已经＋过了
        int id = player.getId();
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
        // suck scale
        boolean moreWithSweep = count > 1 && EnchantmentHelper.getLevel(Enchantments.SWEEPING, itemStack) > 0;
        float scale = level * (1F + (moreWithSweep ? 0.5F : 0F));
        player.heal(scale * amount / 10);
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
        if (!(itemStack.getItem() instanceof SwordItem) || (level = level(Weakness.class, itemStack)) < 1) {
            return amount;
        }
        // 已经判断过了
        int id = player.getId();
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
            if (level(Rebirth.class, armor) > 0) {
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
    public static void moreLoot(LootContext context, LootNumberProvider rolls) {
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
        int level = level(MoreLoot.class, itemStack);
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
        if (ran >= Enchant.option.moreLootRate - 1) {
            return;
        }
        //  5%
        if (ran < Enchant.option.moreMoreLootRate - 1) {
            level *= Enchant.option.moreMoreLootMultiplier;
            sendMessage(MORE_LOOT_TEXT);
        } else {
            sendMessage(LOOT_TEXT);
        }
        try {
            level += 1;
            Field field = ConstantLootNumberProvider.class.getDeclaredField("value");
            field.setAccessible(true);
            float value = field.getFloat(rolls);
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
    public static void resetLoot(LootNumberProvider rolls) {
        if (!ROLLS_MAP.containsKey(rolls)) {
            return;
        }
        try {
            Field field = ConstantLootNumberProvider.class.getDeclaredField("value");
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
        int level = level(HitRateUp.class, itemStack);
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
        return level(QuickShoot.class, itemStack);
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
                && level(MagicImmune.class, player.getEquippedStack(EquipmentSlot.CHEST)) > 0
                && StatusEffectCategory.HARMFUL.equals(effect.getEffectType().getCategory());
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
