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

    public boolean clickAutoFishing() {
        return autoFishing = !autoFishing;
    }

    public boolean clickSuckBlood() {
        return suckBlood = !suckBlood;
    }

    public boolean clickWeakness() {
        return weakness = !weakness;
    }
}
