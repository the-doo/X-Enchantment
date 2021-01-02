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

/**
 * mod menu 配置界面
 */
public class ModMenuScreen extends Screen {

    private static final TranslatableText AUTO_FISHING_TEXT = new TranslatableText("enchant.menu.option.auto_fish");
    private static final TranslatableText SUCK_BLOOD_TEXT = new TranslatableText("enchant.menu.option.suck_blood");
    private static final TranslatableText WEAKNESS_TEXT = new TranslatableText("enchant.menu.option.weakness");

    private static final ModMenuScreen INSTANCE = new ModMenuScreen();

    private Screen pre;

    private ModMenuScreen() {
        super(new LiteralText(Enchant.ID));
        init();
    }

    @Override
    protected void init() {
        // 自动钓鱼按钮
        this.addButton(new ButtonWidget(this.width / 2 - 150 / 2, 28, 150, 20,
                AUTO_FISHING_TEXT.copy().append(": " + Enchant.option.autoFishing),
                b -> b.setMessage(AUTO_FISHING_TEXT.copy().append(": " + Enchant.option.clickAutoFishing()))));
        // 吸血按钮
        this.addButton(new ButtonWidget(this.width / 2 - 150 / 2, 28 * 2, 150, 20,
                SUCK_BLOOD_TEXT.copy().append(": " + Enchant.option.suckBlood),
                b -> b.setMessage(SUCK_BLOOD_TEXT.copy().append(": " + Enchant.option.clickSuckBlood()))));
        // 弱点攻击按钮
        this.addButton(new ButtonWidget(this.width / 2 - 150 / 2, 28 * 3, 150, 20,
                WEAKNESS_TEXT.copy().append(": " + Enchant.option.weakness),
                b -> b.setMessage(WEAKNESS_TEXT.copy().append(": " + Enchant.option.clickWeakness()))));
        // 返回按钮
        this.addButton(new ButtonWidget(this.width / 2 - 150 / 2, this.height - 28, 150, 20,
                ScreenTexts.BACK, b -> INSTANCE.close()));
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
