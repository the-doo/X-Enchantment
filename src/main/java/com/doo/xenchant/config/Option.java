package com.doo.xenchant.config;

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
    public boolean reborn = true;

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
     * 光环触发时刻
     */
    public double haloInterval = 20F;

    /**
     * 攻速光环
     */
    public boolean attackSpeedHalo = true;

    /**
     * 攻速光环 倍数
     */
    public double attackSpeedHaloMultiple = 1;

    /**
     * 幸运光环
     */
    public boolean luckHalo = true;

    /**
     * 幸运光环 是否是宝藏
     */
    public boolean luckHaloIsTreasure = true;

    /**
     * 幸运光环 持续时间 单位: s
     */
    public int luckHaloDuration = 1;

    /**
     * 幸运光环  级别
     */
    public int luckHaloLevel = 3;

    /**
     * 最大生命值光环
     */
    public boolean maxHPHalo = true;

    /**
     * 最大生命光环 倍数
     */
    public double maxHPHaloMultiple = 1;

    /**
     * 恢复光环
     */
    public boolean regenerationHalo = true;

    /**
     * 恢复光环 持续时间 单位: s
     */
    public int regenerationHaloDuration = 1;

    /**
     * 恢复光环  级别
     */
    public int regenerationHaloLevel = 2;

    /**
     * 减速光环
     */
    public boolean slownessHalo = true;

    /**
     * 减速光环 持续时间 单位: s
     */
    public int slownessHaloDuration = 1;

    /**
     * 减速光环  级别
     */
    public int slownessHaloLevel = 2;

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
}
