package com.doo.xenchantment.util;

import com.doo.playerinfo.core.InfoGroupItems;
import com.doo.playerinfo.core.InfoItemCollector;
import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.enchantment.*;
import com.doo.xenchantment.enchantment.curse.*;
import com.doo.xenchantment.enchantment.halo.AlliedBonus;
import com.doo.xenchantment.enchantment.halo.BurnWell;
import com.doo.xenchantment.enchantment.halo.FarmSpeed;
import com.doo.xenchantment.enchantment.halo.Halo;
import com.doo.xenchantment.enchantment.special.*;
import com.doo.xenchantment.events.LootApi;
import com.doo.xenchantment.interfaces.*;
import com.doo.xenchantment.screen.OptionScreen;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 附魔工具
 */
public class EnchantUtil {
    public static final List<Class<? extends Halo>> HALO_CLASS = Lists.newArrayList();
    public static final List<CanDeath<? extends BaseXEnchantment>> DEATH_ENCHANT = Lists.newArrayList();
    public static final List<Advable<? extends BaseXEnchantment>> ADV_ENCHANT = Lists.newArrayList();
    public static final List<WithAttribute<? extends BaseXEnchantment>> ATTR_ENCHANT = Lists.newArrayList();
    public static final List<Tooltipsable<? extends BaseXEnchantment>> TIPS_ENCHANT = Lists.newArrayList();
    public static final Map<Enchantment, Usable<? extends BaseXEnchantment>> USE_ENCHANT_MAP = Maps.newHashMap();

    public static final Map<Class<? extends BaseXEnchantment>, BaseXEnchantment> ENCHANTMENTS_MAP = new LinkedHashMap<>();

    public static final EquipmentSlot[] ALL_ARMOR = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public static final ResourceLocation CONFIG_CHANNEL = new ResourceLocation(XEnchantment.MOD_ID, "config_loading");

    private static MinecraftServer server;
    private static JsonObject allOption;

    private EnchantUtil() {
    }

    /**
     * 注册所有附魔及事件
     */
    public static void registerAll(XEnchantmentRegistry registry) {
        Stream<? extends BaseXEnchantment> stream = Stream.of(
                // other
                MoreLoot.class, BrokenDawn.class, ProtectionAnvil.class,
                // attack
                NightBreak.class, SuckBlood.class, Weakness.class, SoulHit.class,
                IncDamage.class, IgnoredArmor.class, AttackSpeed.class,
                // bow
                ShootSpeed.class, Diffusion.class, Elasticity.class,
                // rod
                AutoFish.class, Librarian.class,
                // armor
                WithEffect.class,
                // head
                Smart.class, FoodBonus.class,
                // chest
                Rebirth.class, MagicImmune.class,
                // leg
                Climber.class, KingKongLegs.class, HeightAttacked.class,
                // feet
                WalkOn.class, JumpAndJump.class, Timor.class
                ,
                // cursed
                Regicide.class, Thin.class, DownDamage.class, DownArmor.class, Insanity.class
                ,
                // Special
                RemoveCursed.class, HealthConverter.class, InfinityEnhance.class, GoBack.class,
                Disenchantment.class, TpToPlayer.class, TimeFaster.class
        ).map(BaseXEnchantment::get).filter(Objects::nonNull);

        // Halo
        List<Class<? extends Halo>> halos = Lists.newArrayList(
                FarmSpeed.class, AlliedBonus.class, BurnWell.class
        );
        HALO_CLASS.addAll(halos);
        Stream<? extends BaseXEnchantment> stream2 =
                Arrays.stream(ALL_ARMOR).flatMap(s -> halos.stream().map(h -> Halo.get(h, s)));

        // merge
        Stream.concat(stream, stream2).sorted(Comparator.comparingInt((BaseXEnchantment e) -> e.isCurse() ? 1 : 0)
                .thenComparingInt((BaseXEnchantment e) -> e instanceof Special ? 1 : 0)
                .thenComparingInt((BaseXEnchantment e) -> e instanceof Halo ? 1 : 0)
                .thenComparingInt((BaseXEnchantment e) -> e.getRarity().getWeight())
        ).forEach(e -> {
            e.register(registry);
            ENCHANTMENTS_MAP.putIfAbsent(e.getClass(), e);

            if (e instanceof Advable<?> o) {
                ADV_ENCHANT.add(o);
            }

            if (e instanceof WithAttribute<?> o) {
                ATTR_ENCHANT.add(o);
            }

            if (e instanceof Tooltipsable<?> o) {
                TIPS_ENCHANT.add(o);
            }

            if (e instanceof Usable<?> o) {
                USE_ENCHANT_MAP.put(e, o);
            }

            if (e instanceof CanDeath<?> o) {
                DEATH_ENCHANT.add(o);
            }
        });

        registerPlayerInfo();
    }

    /**
     * 注册所有附魔及事件
     */
    public static void registerPlayerInfo() {
        InfoItemCollector.register(XEnchantment.MOD_NAME, player -> {
            List<InfoGroupItems> sorted = Lists.newArrayList();
            ENCHANTMENTS_MAP.values().stream().filter(e -> !e.disabled())
                    .map(e -> e.collectPlayerInfo(player))
                    .filter(it -> it != null && !it.isEmpty())
                    .forEach(sorted::add);
            return sorted;
        });
    }

    public static void registerAttr(Consumer<WithAttribute<?>> registry) {
        ATTR_ENCHANT.forEach(registry);
    }

    public static void registerAdv(Consumer<Advable<?>> registry) {
        ADV_ENCHANT.forEach(registry);
    }

    public static void registerToolTips(Consumer<Tooltipsable<?>> registry) {
        TIPS_ENCHANT.forEach(registry);
    }

    public static void onClient() {
        ENCHANTMENTS_MAP.values().forEach(e -> {
            e.onClient();
            e.onOptionsRegister((k, v) -> OptionScreen.register(e.optGroup(), k, v));
        });
    }

    public static void onServer(MinecraftServer server) {
        EnchantUtil.server = server;

        // load init config
        configLoad(ConfigUtil.load());

        ENCHANTMENTS_MAP.values().forEach(e -> e.onServer(server));
    }

    public static void onServerStarted() {
        ENCHANTMENTS_MAP.values().forEach(BaseXEnchantment::onServerStarted);
    }

    public static void onKilled(Consumer<BaseXEnchantment> consumer) {
        ENCHANTMENTS_MAP.values().stream().filter(e -> !e.disabled()).forEach(consumer);
    }

    public static void canDeath(Consumer<CanDeath<?>> consumer) {
        DEATH_ENCHANT.forEach(consumer);
    }

    public static void endLivingTick(LivingEntity entity) {
        ENCHANTMENTS_MAP.values().stream()
                .filter(e -> !e.disabled() && !(e instanceof Halo))
                .forEach(e -> e.onEndTick(entity));
        // halo - trigger once
        HALO_CLASS.forEach(h -> Halo.onEndLiving(entity, (Halo) ENCHANTMENTS_MAP.get(h)));
    }

    public static boolean canBeAffected(MobEffectInstance effect, LivingEntity living) {
        return ENCHANTMENTS_MAP.values().stream()
                .filter(e -> !e.disabled())
                .allMatch(e -> e.canBeAffected(effect, living));
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

    public static void lootMob(DamageSource damageSource, List<ItemStack> additionLoot, Consumer<List<ItemStack>> callback) {
        Entity entity = damageSource.getEntity();
        if (!(entity instanceof LivingEntity living)) {
            return;
        }

        ItemStack stack = living.getMainHandItem();
        List<ItemStack> trigger = LootApi.trigger(living, living.getRandom(), stack, additionLoot, true);
        if (!trigger.isEmpty()) {
            callback.accept(trigger);
        }
    }

    public static void lootFishing(Player player, List<ItemStack> list, Consumer<List<ItemStack>> callback) {
        if (player == null) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        List<ItemStack> trigger = LootApi.trigger(player, player.getRandom(), stack, list, true);
        if (!trigger.isEmpty()) {
            callback.accept(trigger);
        }
    }

    public static void lootBlock(Entity entity, ItemStack itemStack, List<ItemStack> list, Consumer<List<ItemStack>> consumer) {
        if (!(entity instanceof LivingEntity player) || list.isEmpty()) {
            return;
        }

        List<ItemStack> trigger = LootApi.trigger(player, player.getRandom(), itemStack, list, false);
        if (!trigger.isEmpty()) {
            consumer.accept(trigger);
        }
    }

    public static int lootShearsFabric(Player player, ItemStack stack, int amount) {
        if (amount < 1 || player == null || stack == null) {
            return amount;
        }

        ItemStack test = Items.COAL.getDefaultInstance();
        List<ItemStack> trigger = LootApi.trigger(player, player.getRandom(), stack, Collections.singletonList(test), true);
        if (trigger.isEmpty()) {
            return amount;
        }

        return amount * trigger.stream().mapToInt(ItemStack::getCount).sum();
    }

    public static void lootShearsForge(List<ItemStack> drop, Player playerIn, LivingEntity living, ItemStack stack) {
        if (drop.isEmpty()) {
            return;
        }

        drop.addAll(LootApi.trigger(playerIn, living.getRandom(), stack, drop, true));
    }

    public static boolean canStandOnFluid(LivingEntity living, BlockPos pos, FluidState fluidState) {
        if (!fluidState.isSource() || !living.level().getFluidState(pos.atY(pos.getY() + 1)).isEmpty()
                || fluidState.getTags().anyMatch(t -> living.getFluidHeight(t) > 0.5)) {
            return false;
        }

        return living instanceof Player && Optional.of(ENCHANTMENTS_MAP.get(WalkOn.class))
                .stream().anyMatch(e -> ((WalkOn) e).canStandOnFluid(living, fluidState));
    }

    public static boolean canEntityWalkOnPowderSnow(Entity entity) {
        return entity instanceof LivingEntity e &&
                ((WalkOn) ENCHANTMENTS_MAP.get(WalkOn.class)).canEntityWalkOnPowderSnow(e);
    }

    public static void configLoad(JsonObject config) {
        if (config == null || config.size() < 1) {
            allOption = getCurrentConfig();
            return;
        }

        allOption = config;
        ENCHANTMENTS_MAP.forEach((k, e) ->
                Optional.ofNullable(config.getAsJsonObject(e.name())).ifPresent(e::loadOptions));
    }

    public static JsonObject allOptions() {
        return allOption;
    }

    public static void allOptionsAfterReloading(Consumer<JsonObject> configConsumer) {
        JsonObject object = getCurrentConfig();
        configConsumer.accept(object);

        // server load
        configLoad(object);

        if (server != null) {
            server.getPlayerList().getPlayers().forEach(p -> ATTR_ENCHANT.forEach(e -> e.reloadAttr(p)));

            // client load
            FriendlyByteBuf buf = ServersideChannelUtil.getJsonBuf(object);
            server.getPlayerList().getPlayers().forEach(p ->
                    ServersideChannelUtil.send(p, CONFIG_CHANNEL, buf, object));
        }
    }

    @NotNull
    private static JsonObject getCurrentConfig() {
        JsonObject object = new JsonObject();
        ENCHANTMENTS_MAP.forEach((k, e) -> object.add(e.name(), e.getOptions()));
        return object;
    }

    public static boolean useOnBlock(BlockPos pos, ItemStack stack, Player player, InteractionHand hand,
                                     Consumer<InteractionResult> consumer) {
        BlockState state = player.level().getBlockState(pos);
        Block block = state.getBlock();
        BlockEntity entity = state.hasBlockEntity() ? player.level().getBlockEntity(pos) : null;
        return EnchantmentHelper.getEnchantments(stack).entrySet().stream()
                .filter(entry -> entry.getKey() instanceof BaseXEnchantment e && !e.disabled() && entry.getValue() != null && entry.getValue() > 0 && USE_ENCHANT_MAP.containsKey(e))
                .anyMatch(entry -> USE_ENCHANT_MAP.get(entry.getKey()).useOnBlock(entry.getValue(), stack, player, hand, state, block, entity, consumer));
    }

    public static boolean useBook(ItemStack stack, Player player, InteractionHand hand,
                                  Consumer<InteractionResultHolder<ItemStack>> consumer) {
        return EnchantmentHelper.getEnchantments(stack).entrySet().stream()
                .filter(entry -> entry.getKey() instanceof BaseXEnchantment e && !e.disabled() && entry.getValue() != null && entry.getValue() > 0 && USE_ENCHANT_MAP.containsKey(e))
                .anyMatch(entry -> USE_ENCHANT_MAP.get(entry.getKey()).onUsed(entry.getValue(), stack, player, hand, consumer));
    }

    public static boolean useOnEntity(Entity entity, Player player, InteractionHand hand, Consumer<InteractionResult> consumer) {
        if (entity instanceof Player target) {
            ItemStack stack = player.getItemInHand(hand);
            return EnchantmentHelper.getEnchantments(stack).entrySet().stream()
                    .filter(entry -> entry.getKey() instanceof BaseXEnchantment e && !e.disabled() && entry.getValue() != null && entry.getValue() > 0 && USE_ENCHANT_MAP.containsKey(e))
                    .anyMatch(entry -> USE_ENCHANT_MAP.get(entry.getKey()).useOnPlayer(entry.getValue(), stack, player, hand, target, consumer));
        }

        return false;
    }

    public static void onEquipItem(LivingEntity living, EquipmentSlot slot, ItemStack stack) {
        EnchantmentHelper.getEnchantments(stack).entrySet().stream()
                .filter(entry -> entry.getKey() instanceof BaseXEnchantment e && !e.disabled() && entry.getValue() != null && entry.getValue() > 0 && USE_ENCHANT_MAP.containsKey(e) && Arrays.stream(e.slots).anyMatch(s -> s == slot))
                .forEach(entry -> USE_ENCHANT_MAP.get(entry.getKey()).onEquipItem(entry.getValue(), living, slot, stack));
    }
}
