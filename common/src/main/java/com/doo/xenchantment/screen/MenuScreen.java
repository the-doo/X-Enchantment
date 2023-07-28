package com.doo.xenchantment.screen;

import com.doo.xenchantment.util.ConfigUtil;
import com.doo.xenchantment.util.EnchantUtil;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.Collections;

public class MenuScreen extends Screen {

    private static final MenuScreen INSTANCE = new MenuScreen();

    private Screen pre;

    private MenuScreen() {
        super(Component.empty());
    }

    public static MenuScreen get(Screen pre) {
        if (pre == null) {
            Window window = Minecraft.getInstance().getWindow();
            INSTANCE.width = window.getScreenWidth();
            INSTANCE.height = window.getScreenHeight();
            return INSTANCE;
        }

        INSTANCE.width = pre.width;
        INSTANCE.height = pre.height;
        INSTANCE.pre = pre;
        return INSTANCE;
    }

    public static void open(Minecraft minecraft) {
        minecraft.setScreen(get(minecraft.screen));
    }

    @Override
    protected void init() {
        int w = this.width;
        OptionsList list = new OptionsList(minecraft, w, this.height, 32, this.height - 32, 25);

        OptionInstance<?>[] opts = EnchantUtil.ENCHANTMENTS.stream()
                .map(e -> new OptionInstance<>(
                        e.getDescriptionId(),
                        OptionInstance.cachedConstantTooltip(Component.translatable(e.getDescriptionId())),
                        (component, object) -> Component.literal(String.valueOf(e.getMaxLevel())),
                        new OptionInstance.Enum<>(Collections.singletonList(e.getOptions()), null),
                        null, null,
                        c -> minecraft.setScreen(new OptionScreen(this, e.name, c))))
                .toArray(OptionInstance[]::new);
        list.addSmall(opts);

        addRenderableWidget(list);
        addRenderableWidget(new Button.Builder(CommonComponents.GUI_BACK, b -> INSTANCE.close()).bounds(this.width / 2 - 150 / 2, this.height - 28, 150, 20).build());
    }

    public void close() {
        if (minecraft != null) {
            // 返回上个页面
            minecraft.setScreen(this.pre);
            // only server save
            if (!minecraft.isLocalServer()) {
                return;
            }
            // 保存设置的配置
            ConfigUtil.write(EnchantUtil.allOptionsAfterReloading());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        renderDirtBackground(guiGraphics);

        super.render(guiGraphics, i, j, f);
    }
}
