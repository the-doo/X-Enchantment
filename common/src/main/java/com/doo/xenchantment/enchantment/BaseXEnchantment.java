package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.core.InfoGroupItems;
import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.advancements.TrueTrigger;
import com.doo.xenchantment.interfaces.WithOptions;
import com.doo.xenchantment.interfaces.XEnchantmentRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 附魔基类
 */
public abstract class BaseXEnchantment extends Enchantment implements WithOptions {

    static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    protected static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 1s is 20 ticks
     */
    protected static final int SECOND_TICK = 20;

    private static final ChatFormatting[] RATE_COLOR = {ChatFormatting.GRAY, ChatFormatting.GRAY, ChatFormatting.GRAY, ChatFormatting.GOLD};

    protected final JsonObject options = new JsonObject();

    private final ResourceLocation id;

    private final String name;

    protected final EquipmentSlot[] slots;

    public static final String MAX_LEVEL_KEY = "max_level";

    public static final String DISABLED_KEY = "disabled";

    public static final String ONLY_ONE_LEVEL_KEY = "only_one_level";

    public static final String NEED_RECONNECT_KEY = "need_reconnect";

    protected BaseXEnchantment(String name, Rarity weight, EnchantmentCategory type, EquipmentSlot... slotTypes) {
        super(weight, type, slotTypes);
        this.name = name;
        this.id = new ResourceLocation(XEnchantment.MOD_ID, name);
        this.slots = slotTypes;

        initOptions();
    }

    protected boolean onlyOneLevel() {
        return false;
    }

    protected boolean needReconnect() {
        return false;
    }

    @Override
    public final int getMaxLevel() {
        return onlyOneLevel() ? 1 : options.get(MAX_LEVEL_KEY).getAsInt();
    }

    public final boolean disabled() {
        return options.get(DISABLED_KEY).getAsBoolean();
    }

    @Override
    public final boolean isDiscoverable() {
        return !disabled() && super.isDiscoverable();
    }

    @Override
    public final boolean isTradeable() {
        return !disabled() && super.isTradeable();
    }

    public String name() {
        return name;
    }

    @Override
    public @NotNull Component getFullname(int level) {
        Component fullname = super.getFullname(level);
        if (disabled()) {
            fullname = fullname.copy().withStyle(ChatFormatting.STRIKETHROUGH);
        }
        return isCurse() ? fullname : fullname.copy().withStyle(RATE_COLOR[getRarity().ordinal()]);
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

    @Override
    public boolean isTreasureOnly() {
        return getRarity() == Rarity.VERY_RARE || getRarity() == Rarity.RARE || getRarity() == Rarity.UNCOMMON;
    }

    public ResourceLocation getId() {
        return id;
    }

    public String nbtKey(String key) {
        return id.toString() + key;
    }

    public int level(ItemStack item) {
        return item == null || item.isEmpty() ? 0 : EnchantmentHelper.getEnchantments(item).getOrDefault(this, 0);
    }

    public boolean hasAdv() {
        return false;
    }

    public TrueTrigger getAdvTrigger() {
        return null;
    }

    public boolean hasAttr() {
        return false;
    }

    public final void insertAttr(ItemStack stack, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> modifier) {
        if (disabled() || stack.getItem() instanceof EnchantedBookItem) {
            return;
        }

        Item i = stack.getItem();
        if (i instanceof Equipable e && e.getEquipmentSlot() != slot) {
            return;
        }

        if (!(i instanceof Equipable) && slot != EquipmentSlot.MAINHAND) {
            return;
        }

        int level = level(stack);
        if (level < 1) {
            return;
        }

        modifiedAttrMap(stack, level, modifier);
    }

    protected void modifiedAttrMap(ItemStack stack, int level, BiConsumer<Attribute, AttributeModifier> modifier) {

    }


    public void onKilled(ServerLevel world, LivingEntity killer, LivingEntity killedEntity) {
    }

    public boolean canDeath(LivingEntity living) {
        return true;
    }

    public boolean allowEffectAddition(MobEffectInstance effect, LivingEntity living) {
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

    public boolean needTooltips() {
        return false;
    }

    public void tooltip(ItemStack stack, TooltipFlag context, List<Component> lines) {
    }

    public void onServerStarted() {
    }

    public InfoGroupItems collectPlayerInfo(ServerPlayer player) {
        return null;
    }

    public boolean canStandOnFluid(LivingEntity living, FluidState fluidState) {
        return false;
    }


    public final String getInfoKey(String key) {
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

        if (onlyOneLevel()) {
            options.addProperty(ONLY_ONE_LEVEL_KEY, false);
        }

        if (needReconnect()) {
            options.addProperty(NEED_RECONNECT_KEY, false);
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
    }

    protected final void loadIf(JsonObject json, String key) {
        Optional.ofNullable(json.get(key))
                .ifPresent(e -> {
                    if (e.isJsonArray()) {
                        options.add(key, e.getAsJsonArray());
                        return;
                    }

                    try {
                        options.addProperty(key, e.getAsDouble());
                    } catch (Exception ex) {
                        options.addProperty(key, e.getAsBoolean());
                    }
                });
    }

    protected final double getDouble(String optionKey) {
        return options.get(optionKey).getAsDouble();
    }

    protected final int getInt(String optionKey) {
        return options.get(optionKey).getAsInt();
    }

    protected final boolean getBoolean(String optionKey) {
        return options.get(optionKey).getAsBoolean();
    }

    protected final void foreach(String optionKey, Consumer<JsonElement> callback) {
        if (!options.get(optionKey).isJsonArray()) {
            return;
        }

        options.getAsJsonArray(optionKey).forEach(callback);
    }

    public void onOptionsRegister(BiConsumer<String, Supplier<Stream<String>>> register) {
    }

    public boolean canEntityWalkOnPowderSnow(LivingEntity e) {
        return false;
    }
}
