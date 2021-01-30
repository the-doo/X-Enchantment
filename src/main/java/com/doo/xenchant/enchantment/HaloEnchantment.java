package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.attribute.LimitTimeModifier;
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

/**
 * 光环类附魔
 */
public abstract class HaloEnchantment extends BaseEnchantment {

    public static final String NAME = "halo";

    public boolean isFriendTarget;

    public static final List<EntityAttribute> ATTRIBUTES = new ArrayList<>(16);

    public HaloEnchantment(String name, boolean isFriendTarget) {
        super(new Identifier(Enchant.ID, NAME + "." + name),
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
     * @param player 玩家
     * @param level 等级
     * @param friends 队友
     * @param mobs 其他
     */
    public final void tickHalo(PlayerEntity player, Integer level, List<LivingEntity> friends, List<LivingEntity> mobs) {
        if (isFriendTarget) {
            if (friends != null && !friends.isEmpty()) {
                onTarget(player, level, friends);
            }
        } else {
            if (mobs != null && !mobs.isEmpty()) {
                onTarget(player, level, mobs);
            }
        }
    }

    public abstract void onTarget(PlayerEntity player, Integer level, List<LivingEntity> targets);

    /**
     * 添加或更新修改值
     *
     * @param e 修改对象
     * @param attr 修改属性
     */
    public void addOrResetModifier(LivingEntity e, EntityAttributeInstance attr) {
        Optional<EntityAttributeModifier> optional = attr.getModifiers().stream()
                .filter(m -> m.getName().equals(getId().toString()) && m instanceof LimitTimeModifier).findAny();
        if (optional.isPresent()) {
            ((LimitTimeModifier) optional.get()).reset(1.2F);
        } else {
            attr.addTemporaryModifier(LimitTimeModifier.get(
                    getId().toString(), 1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL, e.age + 25, e));
        }
    }
}
