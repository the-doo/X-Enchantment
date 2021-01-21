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
    public boolean rebirth = true;

    /**
     * 更多战利品
     */
    public boolean moreLoot = true;

    /**
     * 无限与修补
     */
    public boolean infinityAcceptMending = true;

    /**
     * 聊天框提示
     */
    public boolean chatTips = true;

    public boolean clickAutoFishing() {
        return autoFishing = !autoFishing;
    }

    public boolean clickSuckBlood() {
        return suckBlood = !suckBlood;
    }

    public boolean clickWeakness() {
        return weakness = !weakness;
    }

    public boolean clickRebirth() {
        return rebirth = !rebirth;
    }

    public boolean clickMoreLoot() {
        return moreLoot = !moreLoot;
    }

    public boolean clickInfinityAcceptMending() {
        return infinityAcceptMending = !infinityAcceptMending;
    }

    public boolean clickChatTips() {
        return chatTips = !chatTips;
    }
}
