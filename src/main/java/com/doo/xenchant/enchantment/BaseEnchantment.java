package com.doo.xenchant.enchantment;

import com.doo.xenchant.config.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

/**
 * 附魔基类
 */
public abstract class BaseEnchantment extends Enchantment {

    private static final Map<Identifier, BaseEnchantment> ID_MAP = new HashMap<>();

    private final Identifier id;

    /**
     * 创建并注册
     *
     * @param id        id
     * @param weight    稀有度
     * @param type      类型
     * @param slotTypes 使用目标
     */
    BaseEnchantment(Identifier id, Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
        this.id = id;

        ID_MAP.put(id, Registry.register(Registry.ENCHANTMENT, id, this));
    }

    /**
     * 获取id
     *
     * @return id
     */
    public Identifier getId() {
        return id;
    }

    public void register() {
    }


    public static <T extends BaseEnchantment> T get(Class<T> clazz) {
        return BaseEnchantmentFactory.getInstance(clazz);
    }


    @SuppressWarnings("all")
    public static <T extends BaseEnchantment> T get(Identifier id) {
        return (T) ID_MAP.get(id);
    }


    @SuppressWarnings("all")
    public static <T extends BaseEnchantment> T get(NbtCompound tag) {
        return (T) ID_MAP.get(EnchantmentHelper.getIdFromNbt(tag));
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

            CACHE.put(clazz, e);
            return (T) e;
        }
    }
}
