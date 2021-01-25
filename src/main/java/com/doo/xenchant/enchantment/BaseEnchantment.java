package com.doo.xenchant.enchantment;

import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

/**
 * 附魔基类
 */
public abstract class BaseEnchantment extends Enchantment {

    private final Identifier id;

    /**
     * 创建并注册
     *
     * @param id id
     * @param weight 稀有度
     * @param type 类型
     * @param slotTypes 使用目标
     */
    public BaseEnchantment(Identifier id, Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
        this.id = id;
        Registry.register(Registry.ENCHANTMENT, id, this);
        EnchantUtil.ENCHANTMENT_MAP.put(id.toString(), this);
    }

    /**
     * 获取id
     *
     * @return id
     */
    public Identifier getId() {
        return id;
    }
}
