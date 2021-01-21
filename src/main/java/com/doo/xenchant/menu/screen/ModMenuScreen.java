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

    private static final TranslatableText AUTO_FISHING_TEXT = new TranslatableText("enchant.menu.option.auto_fish");
    private static final TranslatableText SUCK_BLOOD_TEXT = new TranslatableText("enchant.menu.option.suck_blood");
    private static final TranslatableText WEAKNESS_TEXT = new TranslatableText("enchant.menu.option.weakness");
    private static final TranslatableText REBIRTH_TEXT = new TranslatableText("enchant.menu.option.rebirth");
    private static final TranslatableText MORE_LOOT_TEXT = new TranslatableText("enchant.menu.option.more_loot");
    private static final TranslatableText INFINITY_ACCEPT_MENDING_TEXT = new TranslatableText("enchant.menu.option.infinity_accept_mending");

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
        this.addButton(new ButtonWidget(x, y * count++, bw, bh,
                AUTO_FISHING_TEXT.copy().append(": " + Enchant.option.autoFishing),
                b -> b.setMessage(AUTO_FISHING_TEXT.copy().append(": " + Enchant.option.clickAutoFishing()))));
        // 吸血
        this.addButton(new ButtonWidget(x, y * count++, bw, bh,
                SUCK_BLOOD_TEXT.copy().append(": " + Enchant.option.suckBlood),
                b -> b.setMessage(SUCK_BLOOD_TEXT.copy().append(": " + Enchant.option.clickSuckBlood()))));
        // 弱点攻击
        this.addButton(new ButtonWidget(x, y * count++, bw, bh,
                WEAKNESS_TEXT.copy().append(": " + Enchant.option.weakness),
                b -> b.setMessage(WEAKNESS_TEXT.copy().append(": " + Enchant.option.clickWeakness()))));
        //  重生
        this.addButton(new ButtonWidget(x, y * count++, bw, bh,
                REBIRTH_TEXT.copy().append(": " + Enchant.option.rebirth),
                b -> b.setMessage(REBIRTH_TEXT.copy().append(": " + Enchant.option.clickRebirth()))));
        //  更多战利品
        this.addButton(new ButtonWidget(x, y * count++, bw, bh,
                MORE_LOOT_TEXT.copy().append(": " + Enchant.option.moreLoot),
                b -> b.setMessage(MORE_LOOT_TEXT.copy().append(": " + Enchant.option.clickMoreLoot()))));
        //  无限与修补
        this.addButton(new ButtonWidget(x, y * count++, bw, bh,
                INFINITY_ACCEPT_MENDING_TEXT.copy().append(": " + Enchant.option.infinityAcceptMending),
                b -> b.setMessage(INFINITY_ACCEPT_MENDING_TEXT.copy().append(": " + Enchant.option.clickInfinityAcceptMending()))));
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
