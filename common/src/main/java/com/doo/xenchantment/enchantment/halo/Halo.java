package com.doo.xenchantment.enchantment.halo;

import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.enchantment.BaseXEnchantment;
import com.google.gson.JsonObject;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Map;

public abstract class Halo extends BaseXEnchantment {

    public static final String INTERVAL_KEY = "interval";
    public static final String RANGE_KEY = "range";
    public static final String PLAYER_ONLY_KEY = "player_only";

    private final JsonObject opts = new JsonObject();
    protected final String haloName;

    protected Halo(String name, EquipmentSlot slot) {
        super("halo." + name + "." + slot.getName(), Rarity.UNCOMMON, EnchantmentCategory.ARMOR, slot);


        haloName = "%s.halo.%s".formatted(XEnchantment.MOD_ID, name);
        if (opts.has(haloName)) {
            return;
        }

        opts.add(haloName, options);

        super.initOptions();

        options.addProperty(INTERVAL_KEY, 1);
        options.addProperty(RANGE_KEY, 5);
        options.addProperty(PLAYER_ONLY_KEY, true);
    }

    @Override
    public final void initOptions() {
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, INTERVAL_KEY);
        loadIf(json, RANGE_KEY);
        loadIf(json, PLAYER_ONLY_KEY);
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

    private int interval() {
        return (int) (getDouble(INTERVAL_KEY) * SECOND_TICK);
    }

    private int range() {
        return (int) getDouble(RANGE_KEY);
    }

    public static void onEndLiving(LivingEntity living, Halo halo) {
        if (living.tickCount % halo.interval() != 0 || halo.disabled() || halo.getBoolean(PLAYER_ONLY_KEY) && !(living instanceof Player)) {
            return;
        }

        ArmorItem ai;
        for (ItemStack slot : living.getArmorSlots()) {
            ai = (ArmorItem) slot.getItem();
            if (halo.level(slot.getEnchantmentTags(), ai.getEquipmentSlot().getName()) < 1) {
                return;
            }
        }

        halo.trigger(living, halo.range());
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

    protected abstract void trigger(LivingEntity living, int range);


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
    public JsonObject getOptions() {
        return opts.getAsJsonObject(haloName);
    }
}
