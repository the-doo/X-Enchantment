package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.enchantment.BaseEnchantment;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;

import java.util.List;

/**
 * Enchantment of Halo
 */
public abstract class HaloEnchantment<T extends Entity> extends BaseEnchantment {

    public static final String NAME = "halo";

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
        if (!Enchant.option.halo || ban()) {
            return;
        }

        // trigger halo
        Box box = living.getBoundingBox().expand(Enchant.option.haloRange);
        halo(living, level, box);
    }

    protected boolean ban() {
        return false;
    }

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
}
