package com.doo.xenchant.menu.screen;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.config.Config;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.Level;

/**
 * mod menu 配置界面
 */
public class ModMenuScreen extends Screen {

    private static final TranslatableText AUTO_FISHING_TEXT = new TranslatableText("x_enchant.menu.option.auto_fish");
    private static final TranslatableText SUCK_BLOOD_TEXT = new TranslatableText("x_enchant.menu.option.suck_blood");
    private static final TranslatableText WEAKNESS_TEXT = new TranslatableText("x_enchant.menu.option.weakness");
    private static final TranslatableText REBIRTH_TEXT = new TranslatableText("x_enchant.menu.option.rebirth");
    private static final TranslatableText MORE_LOOT_TEXT = new TranslatableText("x_enchant.menu.option.more_loot");
    private static final TranslatableText INFINITY_ACCEPT_MENDING_TEXT = new TranslatableText("x_enchant.menu.option.infinity_accept_mending");
    private static final TranslatableText HIT_RATE_UP_TEXT = new TranslatableText("x_enchant.menu.option.hit_rate_up");
    private static final TranslatableText QUICK_SHOOT_TEXT = new TranslatableText("x_enchant.menu.option.quick_shoot");
    private static final TranslatableText MAGIC_IMMUNE_TEXT = new TranslatableText("x_enchant.menu.option.magic_immune");
    private static final TranslatableText HALO_TEXT = new TranslatableText("x_enchant.menu.option.halo");

    private static final TranslatableText CHAT_TIPS_TEXT = new TranslatableText("x_enchant.menu.option.chat_tips");

    private static final ModMenuScreen INSTANCE = new ModMenuScreen();

    private Screen pre;

    private ModMenuScreen() {
        super(new LiteralText(Enchant.ID));
        init();
    }

    @Override
    protected void init() {
        int bw = 150;
        int bh = 20;
        int y = 28;
        int x = this.width / 2 - bw / 2;
        int count = 1;
        // 自动钓鱼
        this.addButton(new ButtonWidget(x - bw / 2, y * count, bw, bh,
                AUTO_FISHING_TEXT.copy().append(": " + Enchant.option.autoFishing),
                b -> b.setMessage(AUTO_FISHING_TEXT.copy().append(": " + Enchant.option.clickAutoFishing()))));
        // 吸血
        this.addButton(new ButtonWidget(x + bw / 2, y * count++, bw, bh,
                SUCK_BLOOD_TEXT.copy().append(": " + Enchant.option.suckBlood),
                b -> b.setMessage(SUCK_BLOOD_TEXT.copy().append(": " + Enchant.option.clickSuckBlood()))));
        // 弱点攻击
        this.addButton(new ButtonWidget(x - bw / 2, y * count, bw, bh,
                WEAKNESS_TEXT.copy().append(": " + Enchant.option.weakness),
                b -> b.setMessage(WEAKNESS_TEXT.copy().append(": " + Enchant.option.clickWeakness()))));
        //  重生
        this.addButton(new ButtonWidget(x + bw / 2, y * count++, bw, bh,
                REBIRTH_TEXT.copy().append(": " + Enchant.option.rebirth),
                b -> b.setMessage(REBIRTH_TEXT.copy().append(": " + Enchant.option.clickRebirth()))));
        //  更多战利品
        this.addButton(new ButtonWidget(x - bw / 2, y * count, bw, bh,
                MORE_LOOT_TEXT.copy().append(": " + Enchant.option.moreLoot),
                b -> b.setMessage(MORE_LOOT_TEXT.copy().append(": " + Enchant.option.clickMoreLoot()))));
        //  无限与修补
        this.addButton(new ButtonWidget(x + bw / 2, y * count++, bw, bh,
                INFINITY_ACCEPT_MENDING_TEXT.copy().append(": " + Enchant.option.infinityAcceptMending),
                b -> b.setMessage(INFINITY_ACCEPT_MENDING_TEXT.copy().append(": " + Enchant.option.clickInfinityAcceptMending()))));
        //  命中率提升
        this.addButton(new ButtonWidget(x - bw / 2, y * count, bw, bh,
                HIT_RATE_UP_TEXT.copy().append(": " + Enchant.option.hitRateUp),
                b -> b.setMessage(HIT_RATE_UP_TEXT.copy().append(": " + Enchant.option.clickHitRateUp()))));
        //  快速射击
        this.addButton(new ButtonWidget(x + bw / 2, y * count++, bw, bh,
                QUICK_SHOOT_TEXT.copy().append(": " + Enchant.option.quickShoot),
                b -> b.setMessage(QUICK_SHOOT_TEXT.copy().append(": " + Enchant.option.clickQuickShoot()))));
        //  聊天框提示
        this.addButton(new ButtonWidget(x - bw / 2, y * count, bw, bh,
                CHAT_TIPS_TEXT.copy().append(": " + Enchant.option.chatTips),
                b -> b.setMessage(CHAT_TIPS_TEXT.copy().append(": " + Enchant.option.clickChatTips()))));
        //  魔免
        this.addButton(new ButtonWidget(x + bw / 2, y * count++, bw, bh,
                MAGIC_IMMUNE_TEXT.copy().append(": " + Enchant.option.magicImmune),
                b -> b.setMessage(MAGIC_IMMUNE_TEXT.copy().append(": " + Enchant.option.clickMagicImmune()))));
        //  光环
        this.addButton(new ButtonWidget(x - bw / 2, y * count, bw, bh,
                HALO_TEXT.copy().append(": " + Enchant.option.halo),
                b -> b.setMessage(HALO_TEXT.copy().append(": " + Enchant.option.clickHalo()))));
        // 返回按钮
        this.addButton(new ButtonWidget(x, this.height - y, bw, bh,
                ScreenTexts.BACK, b -> INSTANCE.close()));
        Config.LOGGER.log(Level.DEBUG, "总共有{}个按钮", count);
    }

    public static ModMenuScreen get(Screen pre) {
        INSTANCE.width = pre.width;
        INSTANCE.height = pre.height;
        INSTANCE.pre = pre;
        return INSTANCE;
    }

    private void close() {
        if (client != null) {
            // 返回上个页面
            client.currentScreen = this.pre;
            // 保存设置的配置
            Config.write(Enchant.ID, Enchant.option);
            // 重新注册
            EnchantUtil.registerAll();
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // 画背景
        super.renderBackground(matrices);
        // 画其他
        super.render(matrices, mouseX, mouseY, delta);
    }
}
