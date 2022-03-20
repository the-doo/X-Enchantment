package com.doo.xenchant.config;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 设置选项
 */
public class Option {

    public Map<String, Map<String, Object>> settings = new HashMap<>();

    /**
     * Disabled Enchantment, contain class name
     */
    public Collection<String> disabled = new HashSet<>();

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
     * weakness attack chance
     */
    public double weaknessChance = 15;

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
    public double moreLootRate = 60;

    /**
     * 更多战利品暴击几率
     */
    public double moreMoreLootRate = 5;

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
    public boolean quickShot = true;

    /**
     * 魔免
     */
    public boolean magicImmune = true;

    /**
     * Diffusion
     */
    public boolean diffusion = true;

    /**
     * Diffusion base value
     */
    public double diffusionDamage = 5;

    /**
     * Night Break Max Level
     */
    public int nightBreakMaxLevel = 5;

    /**
     * Night Break Per Level
     */
    public double nightBreakPerLevel = 10;

    /**
     * BrokenDawn
     */
    public boolean brokenDawn = true;

    /**
     * BrokenDawn Process
     */
    public double brokenDawnProcess = 1.5;

    /**
     * BrokenDawn Success
     */
    public double brokenDawnSuccess = 20;

    /**
     * 光环
     */
    public boolean halo = true;

    /**
     * 光环范围
     */
    public double haloRange = 9F;

    /**
     * Harmful Target Is Monster
     */
    public boolean harmfulTargetOnlyMonster = true;

    /**
     * 光环能否被其他东西使用
     */
    public AllowTarget haloAllowOther = AllowTarget.ALL;

    public enum AllowTarget {
        ALL(new TranslatableComponent("x_enchant.menu.option.halo_allow_target.all")),
        PLAYER(new TranslatableComponent("x_enchant.menu.option.halo_allow_target.player")),

        ;

        public final Component key;

        AllowTarget(Component key) {
            this.key = key;
        }
    }

    /**
     * 雷霆光环
     */
    public boolean thunderHalo = true;

    /**
     * Thunder Halo allow target
     */
    public AllowTarget thunderHaloAllowOther = AllowTarget.ALL;

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
                    Attributes.ATTACK_DAMAGE,
                    Attributes.ATTACK_SPEED,
                    Attributes.ARMOR,
                    Attributes.ARMOR_TOUGHNESS,
                    Attributes.KNOCKBACK_RESISTANCE,
                    Attributes.FLYING_SPEED,
                    Attributes.MOVEMENT_SPEED,
                    Attributes.MAX_HEALTH,
                    Attributes.ATTACK_KNOCKBACK)
            .map(Attribute::getDescriptionId).collect(Collectors.toSet());

    /**
     * Effect Halo Level of friendly
     */
    public int effectTreasureMaxLevel = 3;

    /**
     * Effect Halo Level of Harmful
     */
    public int effectOtherMaxLevel = 3;

    /**
     * Disabled All Effect Set
     */
    public boolean enabledAllEffect = true;

    /**
     * Only Potion Effect
     */
    public boolean onlyPotionEffect = true;

    /**
     * Disabled Effect Set
     */
    public Collection<String> disabledEffect = new HashSet<>();

    /**
     * special
     */
    public boolean special = false;

    /**
     * trinkets
     */
    public boolean trinkets = false;
}
