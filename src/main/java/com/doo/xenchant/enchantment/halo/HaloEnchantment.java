package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.attribute.LimitTimeModifier;
import com.doo.xenchant.enchantment.BaseEnchantment;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 光环类附魔
 */
public abstract class HaloEnchantment<T extends Entity> extends BaseEnchantment {

    public static final String NAME = "halo";

    public static final List<EntityAttribute> ATTRIBUTES = new ArrayList<>(16);

    public HaloEnchantment(String name) {
        super(NAME + "_" + name, Rarity.RARE, EnchantmentTarget.ARMOR, EnchantUtil.ALL_ARMOR);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public final int getMinPower(int level) {
        return 20;
    }

    @Override
    public final int getMaxPower(int level) {
        return 50;
    }

    @Override
    protected final boolean canAccept(Enchantment other) {
        return !(other instanceof HaloEnchantment);
    }

    @Override
    public void livingTick(LivingEntity living, ItemStack stack, int level) {
        if (!Enchant.option.halo && needTick()) {
            return;
        }

        // trigger halo
        Box box = living.getBoundingBox().expand(Enchant.option.haloRange);
        halo(living, level, box);
    }

    protected abstract boolean needTick();

    /**
     * trigger halo
     */
    private void halo(LivingEntity living, Integer level, Box box) {
        List<T> targets = targets(living, box);
        if (targets != null && !targets.isEmpty()) {
            onTarget(living, level, targets);
        }
    }

    protected abstract List<T> targets(LivingEntity living, Box box);

    protected abstract void onTarget(LivingEntity entity, Integer level, List<T> targets);

    /**
     * 添加或更新修改值
     *
     * @param attr     修改的属性
     * @param modifier 修改值 默认值
     */
    public void addOrResetModifier(EntityAttributeInstance attr, LimitTimeModifier modifier) {
        Optional<EntityAttributeModifier> optional = attr.getModifiers().stream()
                .filter(m -> m.getName().equals(getId().toString()) && m instanceof LimitTimeModifier).findAny();
        if (optional.isPresent()) {
            ((LimitTimeModifier) optional.get()).reset(1.2F);
        } else {
            attr.addTemporaryModifier(modifier);
        }
    }
}
