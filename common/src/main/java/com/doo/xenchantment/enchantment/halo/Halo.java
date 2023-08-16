package com.doo.xenchantment.enchantment.halo;

import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.enchantment.BaseXEnchantment;
import com.doo.xenchantment.util.EnchantUtil;
import com.google.gson.JsonObject;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;

import java.util.Map;

public abstract class Halo extends BaseXEnchantment {

    public static final String HALO_KEY = "halo";
    public static final String INTERVAL_KEY = "interval";
    public static final String RANGE_KEY = "range";
    public static final String PLAYER_ONLY_KEY = "player_only";

    private static final JsonObject OPTS = new JsonObject();
    protected final String haloName;
    protected final String optName;

    protected Halo(String name, Rarity rarity, EquipmentSlot slot) {
        super("halo." + name + "." + slot.getName(), rarity, EnchantmentCategory.ARMOR, slot);

        haloName = "enchantment.x_enchantment.halo." + name;
        optName = OPT_FORMAT.formatted(XEnchantment.MOD_ID, "halo.") + name;
        if (OPTS.has(haloName)) {
            return;
        }

        OPTS.add(haloName, options);

        super.initOptions();

        options.addProperty(HALO_KEY, true);
        options.addProperty(PLAYER_ONLY_KEY, true);
        options.addProperty(INTERVAL_KEY, 1);
        options.addProperty(RANGE_KEY, 5);

        initHaloFirstOptions();
    }

    protected void initHaloFirstOptions() {
    }


    @Override
    public final void initOptions() {
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, PLAYER_ONLY_KEY);
        loadIf(json, INTERVAL_KEY);
        loadIf(json, RANGE_KEY);
    }

    @Override
    public boolean isDisabled() {
        return EnchantUtil.ENCHANTMENTS_MAP.get(this.getClass()).disabled();
    }

    @Override
    protected boolean onlyOneLevel() {
        return true;
    }

    @Override
    public boolean canEnchant(ItemStack itemStack) {
        return itemStack.getItem() instanceof ArmorItem ai && ai.getEquipmentSlot() == slots[0];
    }

    @Override
    protected boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && !(enchantment instanceof Halo);
    }

    protected int interval() {
        return (int) (getDouble(INTERVAL_KEY) * SECOND_TICK);
    }

    protected double range() {
        return getDouble(RANGE_KEY);
    }

    public static void onEndLiving(LivingEntity living, Halo halo) {
        int interval = halo.interval();
        if (interval < 1 || living.tickCount % interval != 0 || halo.disabled() || halo.getBoolean(PLAYER_ONLY_KEY) && !(living instanceof Player)) {
            return;
        }

        for (ItemStack slot : living.getArmorSlots()) {
            if (!(slot.getItem() instanceof Equipable ei) || halo.level(slot.getEnchantmentTags(), ei.getEquipmentSlot().getName()) < 1) {
                return;
            }
        }

        double range = halo.range();
        if (range <= 0) {
            return;
        }

        halo.trigger(living, living.getBoundingBox().inflate(range));
    }

    private int level(ListTag tag, String slotName) {
        Map<Enchantment, Integer> map = EnchantmentHelper.deserializeEnchantments(tag);
        return map.keySet().stream()
                // only 4
                .filter(e -> e instanceof Halo && e.getClass().isInstance(this))
                // only 1
                .filter(e -> e.getDescriptionId().endsWith(slotName))
                // if it has
                .map(map::get).findAny().orElse(0);
    }

    protected abstract void trigger(LivingEntity living, AABB box);


    /**
     * Can regis to any event or other things
     */
    public static <T extends Halo> T get(Class<T> clazz, EquipmentSlot slot) {
        try {
            return clazz.getDeclaredConstructor(EquipmentSlot.class).newInstance(slot);
        } catch (Exception e) {
            LOGGER.warn("Load x-enchantment {} error", clazz, e);
            return null;
        }
    }

    @Override
    public String name() {
        return haloName;
    }

    @Override
    public String optGroup() {
        return optName;
    }

    @Override
    public JsonObject getOptions() {
        return OPTS.getAsJsonObject(haloName);
    }
}