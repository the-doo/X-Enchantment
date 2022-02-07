package com.doo.xenchant.config;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 设置选项
 */
public class Option {

    /**
     * 自动钓鱼
     */
    public boolean autoFishing = true;

    /**
     * 吸血
     */
    public boolean suckBlood = true;

    /**
     * 弱点攻击
     */
    public boolean weakness = true;

    /**
     * 重生
     */
    public boolean rebirth = true;

    /**
     * 更多战利品
     */
    public boolean moreLoot = true;

    /**
     * 更多战利品几率
     */
    public double moreLootRate = 20;

    /**
     * 更多战利品暴击几率
     */
    public double moreMoreLootRate = 1;

    /**
     * 更多战利品暴击倍数
     */
    public double moreMoreLootMultiplier = 10;

    /**
     * 命中率提升
     */
    public boolean hitRateUp = true;

    /**
     * 快速射击
     */
    public boolean quickShoot = true;

    /**
     * 魔免
     */
    public boolean magicImmune = true;

    /**
     * 无限与修补
     */
    public boolean infinityAcceptMending = true;

    /**
     * 聊天框提示
     */
    public boolean chatTips = true;

    /**
     * 光环
     */
    public boolean halo = true;

    /**
     * 光环范围
     */
    public double haloRange = 9F;

    /**
     * 雷霆光环
     */
    public boolean thunderHalo = true;

    /**
     * 雷霆光环 是否是宝藏
     */
    public boolean thunderHaloIsTreasure = true;

    /**
     * 雷霆光环 击中几率
     */
    public int thunderHaloStruckChance = 10;

    /**
     * attributes
     */
    public Collection<String> attributes = Stream.of(
                    EntityAttributes.GENERIC_ATTACK_DAMAGE,
                    EntityAttributes.GENERIC_ATTACK_SPEED,
                    EntityAttributes.GENERIC_ARMOR,
                    EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
                    EntityAttributes.GENERIC_FLYING_SPEED,
                    EntityAttributes.GENERIC_MOVEMENT_SPEED,
                    EntityAttributes.GENERIC_MAX_HEALTH,
                    EntityAttributes.GENERIC_ATTACK_KNOCKBACK)
            .map(EntityAttribute::getTranslationKey).collect(Collectors.toSet());

    /**
     * Disabled Effect Set
     */
    public Collection<String> disabledEffect = new HashSet<>();
}
