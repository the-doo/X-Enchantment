package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.core.InfoGroupItems;
import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.interfaces.OneLevelMark;
import com.doo.xenchantment.interfaces.WithOptions;
import com.doo.xenchantment.interfaces.XEnchantmentRegistry;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 附魔基类
 */
public abstract class BaseXEnchantment extends Enchantment implements WithOptions {

    static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    public static final String OPT_FORMAT = "%s.menu.option.%s";

    protected static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 1s is 20 ticks
     */
    protected static final int SECOND_TICK = 20;

    protected final JsonObject options = new JsonObject();

    protected final ResourceLocation id;

    private final String name;

    private final String optGroup;

    public final EquipmentSlot[] slots;

    protected final List<Enchantment> compatibility = Lists.newArrayList();

    public static final String MAX_LEVEL_KEY = "max_level";

    public static final String DISABLED_KEY = "disabled";

    public static final String ONLY_ONE_LEVEL_KEY = "only_one_level";

    protected BaseXEnchantment(String name, Rarity weight, EnchantmentCategory type, EquipmentSlot... slotTypes) {
        super(weight, type, slotTypes);
        this.name = name;
        this.id = new ResourceLocation(XEnchantment.MOD_ID, name);
        this.slots = slotTypes;
        this.optGroup = OPT_FORMAT.formatted(XEnchantment.MOD_ID, name);
        initOptions();
    }

    protected static boolean isNotAllied(Entity e, LivingEntity living) {
        return !e.isAlliedTo(living) && (!(e instanceof OwnableEntity o) || !living.isAlliedTo(o.getOwner()));
    }

    protected static void addEffect(LivingEntity e, MobEffectInstance instance) {
        MobEffect effect = instance.getEffect();
        if (!effect.getAttributeModifiers().containsKey(Attributes.MAX_HEALTH) || !e.hasEffect(effect)) {
            e.addEffect(instance);
            return;
        }

        e.getEffect(effect).update(instance);
        if (e instanceof ServerPlayer p) {
            p.connection.send(new ClientboundUpdateMobEffectPacket(e.getId(), instance));
        }
    }

    @Override
    public final int getMaxLevel() {
        return this instanceof OneLevelMark ? 1 : options.get(MAX_LEVEL_KEY).getAsInt();
    }

    @Override
    protected boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && !compatibility.contains(enchantment);
    }

    @Override
    public boolean isDiscoverable() {
        return !disabled() && super.isDiscoverable();
    }

    @Override
    public boolean isTradeable() {
        return !disabled() && super.isTradeable();
    }

    @Override
    public boolean isTreasureOnly() {
        return getRarity() == Rarity.VERY_RARE || getRarity() == Rarity.RARE;
    }

    @Override
    public @NotNull Component getFullname(int level) {
        Component fullname = super.getFullname(level);
        if (disabled()) {
            fullname = fullname.copy().withStyle(ChatFormatting.STRIKETHROUGH);
        }
        return isCurse() ? fullname : fullname.copy().withStyle(getRarity() == Rarity.VERY_RARE ? ChatFormatting.GOLD : ChatFormatting.GRAY);
    }

    @Override
    public int getMinCost(int level) {
        return switch (getRarity()) {
            case UNCOMMON -> level * 20;
            case RARE -> level * 25;
            case VERY_RARE -> level * 30;
            default -> super.getMinCost(level);
        };
    }

    @Override
    public int getMaxCost(int level) {
        return switch (getRarity()) {
            case UNCOMMON -> getMinCost(level) + 35;
            case RARE -> getMinCost(level) + 50;
            case VERY_RARE -> getMinCost(level) + 100;
            default -> super.getMaxCost(level);
        };
    }

    public final boolean disabled() {
        return options.has(DISABLED_KEY) ? options.get(DISABLED_KEY).getAsBoolean() : isDisabled();
    }

    public boolean isDisabled() {
        return false;
    }

    public String name() {
        return name;
    }

    public String menuName() {
        return name;
    }

    public String optGroup() {
        return optGroup;
    }

    public ResourceLocation getId() {
        return id;
    }

    protected final boolean isSameId(Tag tag) {
        if (tag instanceof CompoundTag t) {
            return id.equals(EnchantmentHelper.getEnchantmentId(t));
        }
        return false;
    }

    public String nbtKey(String key) {
        return "%s.%s".formatted(id.toString(), key);
    }

    public int level(ItemStack stack) {
        return stack == null || stack.isEmpty() || stack.getTag() == null || stack.getTag().isEmpty() || !stack.is(Items.ENCHANTED_BOOK) && stack.getEnchantmentTags().isEmpty() ? 0 : EnchantmentHelper.getEnchantments(stack).getOrDefault(this, 0);
    }

    public int totalLevel(Player player) {
        int level = 0;
        for (EquipmentSlot slot : this.slots) {
            level += level(player.getItemBySlot(slot));
        }
        return level;
    }

    public void onKilled(ServerLevel world, LivingEntity killer, LivingEntity killedEntity) {
    }

    public boolean canDeath(LivingEntity living) {
        return true;
    }

    public boolean canBeAffected(MobEffectInstance effect, LivingEntity living) {
        return true;
    }

    /**
     * Can regis to any event or other things
     */
    public void onServer(MinecraftServer server) {
    }

    /**
     * Can regis to any event or other things
     */
    public void onEndTick(LivingEntity living) {
    }

    /**
     * Can register to any event or other things
     */
    public void onClient() {
    }

    public void onServerStarted() {
    }

    public InfoGroupItems collectPlayerInfo(ServerPlayer player) {
        return null;
    }

    public String getInfoKey(String key) {
        return String.format("x_enchantment.info.%s.%s", name, key);
    }

    /**
     * Can regis to any event or other things
     */
    public final void register(XEnchantmentRegistry registry) {
        registry.register(this);
    }

    /**
     * Can regis to any event or other things
     */
    public static <T extends BaseXEnchantment> T get(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            LOGGER.warn("Load x-enchantment {} error", clazz, e);
            return null;
        }
    }

    public void initOptions() {
        options.addProperty(MAX_LEVEL_KEY, 1);
        options.addProperty(DISABLED_KEY, false);
        options.add(COMPATIBILITY_KEY, new JsonArray());

        if (this instanceof OneLevelMark) {
            options.addProperty(ONLY_ONE_LEVEL_KEY, false);
        }
    }

    @Override
    public JsonObject getOptions() {
        return options;
    }

    @Override
    public void loadOptions(JsonObject json) {
        loadIf(json, MAX_LEVEL_KEY);
        loadIf(json, DISABLED_KEY);
        loadIf(json, COMPATIBILITY_KEY);

        compatibility.clear();

        foreach(COMPATIBILITY_KEY, j ->
                BuiltInRegistries.ENCHANTMENT.stream()
                        .filter(e -> e.getDescriptionId().equals(j.getAsString()))
                        .forEach(compatibility::add));
    }

    @Override
    public ChatFormatting optionsTextColor() {
        return getRarity() == Rarity.VERY_RARE ? ChatFormatting.GOLD : WithOptions.super.optionsTextColor();
    }
}
