package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.config.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 附魔基类
 */
public abstract class BaseEnchantment extends Enchantment {

    static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    /**
     * 1s is 20 ticks
     */
    protected static final int SECOND = 20;

    private static final Formatting[] RATE_COLOR = {Formatting.GRAY, Formatting.BLUE, Formatting.YELLOW, Formatting.GOLD};

    private final Identifier id;


    protected BaseEnchantment(String name, Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
        this.id = new Identifier(Enchant.ID, name);
    }

    @Override
    public Text getName(int level) {
        Text name = super.getName(level);
        return isCursed() ? name : name.shallowCopy().formatted(RATE_COLOR[getRarity().ordinal()]);
    }

    public static <T extends BaseEnchantment> T get(Class<T> clazz) {
        return BaseEnchantmentFactory.getInstance(clazz);
    }

    public Identifier getId() {
        return id;
    }

    public String nbtKey(String key) {
        return id.toString() + key;
    }

    public int level(ItemStack item) {
        return EnchantmentHelper.getLevel(this, item);
    }

    /**
     * Can regis to any event or other things
     */
    public void register() {
        // Don't replace if exist
        if (Registry.ENCHANTMENT.containsId(id)) {
            return;
        }

        BaseEnchantmentFactory.register(Registry.register(Registry.ENCHANTMENT, id, this));
    }

    @SuppressWarnings("all")
    private static class BaseEnchantmentFactory {

        private static final Map<Class<? extends BaseEnchantment>, BaseEnchantment> CACHE = new HashMap<>();

        public static <T extends BaseEnchantment> T getInstance(Class<T> clazz) {
            if (CACHE.containsKey(clazz)) {
                return (T) CACHE.get(clazz);
            }

            BaseEnchantment e = null;
            try {
                e = clazz.newInstance();
            } catch (Exception ignore) {
                Config.LOGGER.warn("error to load enchantment {}", clazz.getName());
            }

            return (T) e;
        }

        public static <T extends BaseEnchantment> void register(T t) {
            CACHE.put(t.getClass(), t);
        }
    }
}
