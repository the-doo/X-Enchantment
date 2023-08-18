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
import com.doo.xenchantment.interfaces.WithAttribute;
import com.doo.xenchantment.interfaces.XEnchantmentRegistry;
import com.doo.xenchantment.screen.OptionScreen;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.material.FluidState;
import org.apache.commons.lang3.mutable.MutableBoolean;
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
    public static final List<WithAttribute<? extends BaseXEnchantment>> ATTR_ENCHANT = Lists.newArrayList();

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
                MoreLoot.class, BrokenDawn.class,
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
                RemoveCursed.class, HealthConverter.class, InfinityEnhance.class, GoBack.class
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

            if (e instanceof WithAttribute<?> attribute) {
                ATTR_ENCHANT.add(attribute);
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

    /**
     * 注册所有附魔及事件
     */
    public static void registerAttr(XEnchantmentRegistry registry) {
        ENCHANTMENTS_MAP.values().stream().filter(WithAttribute.class::isInstance).forEach(registry::register);
    }

    public static void registerAdv(XEnchantmentRegistry registry) {
        ENCHANTMENTS_MAP.values().stream().filter(BaseXEnchantment::hasAdv).forEach(registry::register);
    }

    public static void registerToolTips(XEnchantmentRegistry registry) {
        ENCHANTMENTS_MAP.values().stream().filter(BaseXEnchantment::needTooltips).forEach(registry::register);
    }

    public static void onClient() {
        ENCHANTMENTS_MAP.values().forEach(BaseXEnchantment::onClient);
        ENCHANTMENTS_MAP.values().forEach(e -> e.onOptionsRegister((k, v) -> OptionScreen.register(e.optGroup(), k, v)));
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

    public static void canDeath(Consumer<BaseXEnchantment> consumer) {
        ENCHANTMENTS_MAP.values().stream().filter(e -> !e.disabled()).forEach(consumer);
    }

    public static void endLivingTick(LivingEntity entity) {
        ENCHANTMENTS_MAP.values().stream()
                .filter(e -> !e.disabled() && !(e instanceof Halo))
                .forEach(e -> e.onEndTick(entity));
        // halo - trigger once
        HALO_CLASS.forEach(h -> Halo.onEndLiving(entity, (Halo) ENCHANTMENTS_MAP.get(h)));
    }

    public static boolean allowEffectAddition(MobEffectInstance effect, LivingEntity living) {
        MutableBoolean tag = new MutableBoolean(true);
        ENCHANTMENTS_MAP.values().stream().filter(e -> !e.disabled()).forEach(e -> {
            boolean b = e.allowEffectAddition(effect, living);
            if (tag.isTrue()) {
                tag.setValue(b);
            }
        });
        return tag.booleanValue();
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
        if (!(entity instanceof Player player)) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        List<ItemStack> trigger = LootApi.trigger(player, stack, additionLoot, true);
        if (!trigger.isEmpty()) {
            callback.accept(trigger);
        }
    }

    public static void lootFishing(Player player, List<ItemStack> list, Consumer<List<ItemStack>> callback) {
        if (player == null) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        List<ItemStack> trigger = LootApi.trigger(player, stack, list, true);
        if (!trigger.isEmpty()) {
            callback.accept(trigger);
        }
    }

    public static void lootBlock(Entity entity, ItemStack itemStack, List<ItemStack> list, Consumer<List<ItemStack>> consumer) {
        if (!(entity instanceof LivingEntity player) || list.isEmpty()) {
            return;
        }

        List<ItemStack> trigger = LootApi.trigger(player, itemStack, list, false);
        if (!trigger.isEmpty()) {
            consumer.accept(trigger);
        }
    }

    public static boolean canStandOnFluid(LivingEntity living, BlockPos pos, FluidState fluidState) {
        if (!fluidState.isSource() || !living.level().getFluidState(pos.atY(pos.getY() + 1)).isEmpty()) {
            return false;
        }

        return living instanceof Player && ENCHANTMENTS_MAP.values().stream().anyMatch(e -> !e.disabled() && e.canStandOnFluid(living, fluidState));
    }

    public static boolean canEntityWalkOnPowderSnow(Entity entity) {
        return entity instanceof LivingEntity e && ENCHANTMENTS_MAP.get(WalkOn.class).canEntityWalkOnPowderSnow(e);
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

    public static boolean useBook(ItemStack stack, Player player, InteractionHand hand,
                                  Consumer<InteractionResultHolder<ItemStack>> consumer) {
        return EnchantmentHelper.getEnchantments(stack).entrySet().stream().anyMatch(entry ->
                entry.getKey() instanceof BaseXEnchantment e && entry.getValue() > 0 && e.canUsed() && e.onUsed(entry.getValue(), stack, player, hand, consumer));
    }
}
