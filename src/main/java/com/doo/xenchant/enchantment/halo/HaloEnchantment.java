package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.attribute.LimitTimeModifier;
import com.doo.xenchant.enchantment.BaseEnchantment;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * 光环类附魔
 */
public abstract class HaloEnchantment extends BaseEnchantment {

    public static final String NAME = "halo";

    public static final List<EntityAttribute> ATTRIBUTES = new ArrayList<>(16);

    private boolean isFriendTarget;

    public HaloEnchantment(String name, boolean isFriendTarget) {
        super(new Identifier(Enchant.ID, NAME + "_" + name),
                Rarity.COMMON,
                EnchantmentTarget.ARMOR,
                EnchantUtil.ALL_ARMOR);
        this.isFriendTarget = isFriendTarget;
    }

    @Override
    public final int getMaxLevel() {
        return 1;
    }

    @Override
    public final int getMinPower(int level) {
        return 10;
    }

    @Override
    public final int getMaxPower(int level) {
        return 25;
    }

    @Override
    protected final boolean canAccept(Enchantment other) {
        return !(other instanceof HaloEnchantment && this.isTreasure() && other.isTreasure());
    }

    /**
     * 触发光环
     *
     * @param player  玩家
     * @param level   等级
     * @param targetsGetter 目标获取器
     */
    public final void tickHalo(PlayerEntity player, Integer level, Function<Boolean, List<LivingEntity>> targetsGetter) {
        if (!needTick()) {
            return;
        }

        List<LivingEntity> targets = targetsGetter.apply(isFriendTarget);
        if (targets != null && !targets.isEmpty()) {
            onTarget(player, level, targets);
        }
    }

    protected abstract boolean needTick();

    public abstract void onTarget(PlayerEntity player, Integer level, List<LivingEntity> targets);

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
