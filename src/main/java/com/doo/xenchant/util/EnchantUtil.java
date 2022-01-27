package com.doo.xenchant.util;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.attribute.LimitTimeModifier;
import com.doo.xenchant.enchantment.*;
import com.doo.xenchant.enchantment.halo.AttackSpeedUpHalo;
import com.doo.xenchant.enchantment.halo.EffectHalo;
import com.doo.xenchant.enchantment.halo.HaloEnchantment;
import com.doo.xenchant.enchantment.halo.ThunderHalo;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.tool.attribute.v1.ToolManager;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.BiConsumer;
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
    /**
     * 可翻译文本
     */
    public static final MutableText MORE_LOOT_TEXT = new TranslatableText("enchantment.x_enchant.chat.more_more_loot").setStyle(Style.EMPTY.withColor(Formatting.RED));
    /**
     * 吸血记录
     */
    private static final Map<Integer, Integer> SUCK_LOG = new HashMap<>();
    /**
     * 攻击记录
     */
    private static final Map<Integer, Integer> WEAKNESS_LOG = new HashMap<>();

    private EnchantUtil() {
    }

    /**
     * 注册所有附魔及事件
     */
    public static void registerAll() {
        // normal enchantments
        Stream.of(AutoFish.class, SuckBlood.class, Weakness.class, Rebirth.class,
                        MoreLoot.class, HitRateUp.class, QuickShoot.class, MagicImmune.class,
                        Librarian.class, IncDamage.class)
                .forEach(c -> BaseEnchantment.get(c).register());

        // Halo enchantments
        Stream.of(ThunderHalo.class, AttackSpeedUpHalo.class).forEach(c -> BaseEnchantment.get(c).register());

        // Status effect halo must regist after all mod loaded
        // need filter(s -> Identifier.isValid(s.getTranslationKey()))
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            Registry.STATUS_EFFECT.stream().filter(e -> e != null && Identifier.isValid(e.getTranslationKey())).forEach(EffectHalo::new);
        });

        // server listener
        ServerWorldEvents.LOAD.register((server, world) -> {
            SUCK_LOG.clear();
            WEAKNESS_LOG.clear();
        });

        // server listener
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity != null) {
                // remove log
                SUCK_LOG.remove(entity.getId());
                WEAKNESS_LOG.remove(entity.getId());
            }
        });

        // server listener
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.player;
            if (player != null) {
                // remove log
                SUCK_LOG.remove(player.getId());
                WEAKNESS_LOG.remove(player.getId());
            }
        });
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
        Integer age = attacker.getLastAttackTime();
        if (SUCK_LOG.put(id, age) == age) {
            return;
        }

        // attack multi target and has sweep
        long count = itemStack.getItem() instanceof SwordItem ? attacker.world.getNonSpectatingEntities(LivingEntity.class, box).stream().filter(l -> canAttacked(attacker, l)).count() : 0;

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
        Integer age = attack.getLastAttackTime();
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
    public static boolean rebirth(LivingEntity player) {
        ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
        Rebirth rebirth = BaseEnchantment.get(Rebirth.class);

        int level = rebirth.level(stack);
        if (level < 1) {
            return true;
        }

        // use totem effect
        player.setHealth(player.getMaxHealth());
        player.clearStatusEffects();
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 500, 4));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 500, 4));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 500, 4));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 500, 2));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 500, 2));
        player.world.sendEntityStatus(player, (byte) 35);

        // decrement 1 level, see ItemStack.addEnchantment
        stack.getNbt().getList(ItemStack.ENCHANTMENTS_KEY, 10).stream().map(e -> (NbtCompound) e).filter(e -> EnchantmentHelper.getIdFromNbt(e).equals(rebirth.getId())).findFirst().ifPresent(e -> EnchantmentHelper.writeLevelToNbt(e, level - 1));
        return false;
    }

    /**
     * 更多战利品
     *
     * @param rolls   基数
     * @param context 上下文
     */
    public static int loot(LootContext context) {
        ItemStack itemStack = context.get(LootContextParameters.TOOL);
        if (itemStack == null) {
            Entity entity = context.get(LootContextParameters.KILLER_ENTITY);
            if (entity instanceof LivingEntity) {
                itemStack = ((LivingEntity) entity).getMainHandStack();
            }
        }
        if (itemStack == null || itemStack.isEmpty()) {
            return 0;
        }

        // no effect on
        BlockState block = context.get(LootContextParameters.BLOCK_STATE);
        if (block != null && !ToolManager.handleIsEffectiveOn(block, itemStack, null)) {
            return 0;
        }

        // no level
        int level = BaseEnchantment.get(MoreLoot.class).level(itemStack);
        if (level < 1) {
            return 0;
        }

        // 20% 0-19
        int ran = context.getRandom().nextInt(100);
        if (ran >= Enchant.option.moreLootRate - 1) {
            return 0;
        }
        // 1% only 1
        if (ran < Enchant.option.moreMoreLootRate) {
            level *= Enchant.option.moreMoreLootMultiplier;
            sendMessage(MORE_LOOT_TEXT);
        }

        return level;
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
     * living tick
     *
     * @param living living
     */
    public static void livingTick(LivingEntity living) {
        if (living.world.isClient()) {
            return;
        }

        // remove dirty arributes
        EnchantUtil.removedDirtyHalo(living.getAttributes());

        // tick enchantment
        StreamSupport.stream(living.getItemsEquipped().spliterator(), true).forEach(stack -> {
            stack.getEnchantments().stream()
                    .filter(n -> BaseEnchantment.isBase(id(n)))
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

    public static boolean hasAttackDamage(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof RangedWeaponItem || stack.getItem() instanceof ToolItem || !stack.getItem().getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE).isEmpty() || !stack.getItem().getAttributeModifiers(EquipmentSlot.OFFHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE).isEmpty());
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
     * replace common loot to enchantment book
     *
     * @param player
     * @param fishingLoots
     * @param random
     * @return
     */
    public static Collection<ItemStack> replaceEnchantmentBook(Collection<ItemStack> fishingLoots, Random random, ItemStack rod) {
        if (rod.isEmpty()) {
            return fishingLoots;
        }

        int level = BaseEnchantment.get(Librarian.class).level(rod);

        List<ItemStack> enchantments = new ArrayList<>();
        Enchantment enchantment;
        for (ItemStack fishingLoot : fishingLoots) {
            // try to replace --- 5% * level chance
            if (fishingLoot.getRarity() == Rarity.COMMON && random.nextInt(100) < 5 * level) {
                fishingLoot.setCount(0);

                // add rondom enchantment
                enchantment = Registry.ENCHANTMENT.getRandom(random);
                enchantments.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, random.nextInt(enchantment.getMaxLevel()) + 1)));
            }
        }

        if (!enchantments.isEmpty()) {
            fishingLoots.addAll(enchantments);
        }
        return fishingLoots;
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
}
