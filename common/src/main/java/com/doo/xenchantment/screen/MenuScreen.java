package com.doo.xenchantment.screen;

import com.doo.xenchantment.enchantment.BaseXEnchantment;
import com.doo.xenchantment.enchantment.halo.Halo;
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
    private static final Component MODIFY_TIP = Component.translatable("x_enchantment.menu.option.only_server.tip");

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
        if (!minecraft.isLocalServer()) {
            addRenderableWidget(Button.builder(MODIFY_TIP, b -> {
            }).bounds(this.width / 2 - 150 / 2, 10, 150, 20).build());
        }

        int w = this.width;
        OptionsList list = new OptionsList(minecraft, w, this.height, 32, this.height - 32, 25);

        OptionInstance<?>[] opts = EnchantUtil.ENCHANTMENTS_MAP.values().stream()
                .filter(e -> !(e instanceof Halo))
                .map(e -> opt(e.getDescriptionId(), e))
                .toArray(OptionInstance[]::new);
        list.addSmall(opts);

        opts = EnchantUtil.HALO_CLASS.stream()
                .map(EnchantUtil.ENCHANTMENTS_MAP::get)
                .map(e -> opt(e.name(), e))
                .toArray(OptionInstance[]::new);
        list.addSmall(opts);

        addRenderableWidget(list);
        addRenderableWidget(new Button.Builder(CommonComponents.GUI_BACK, b -> INSTANCE.close()).bounds(this.width / 2 - 150 / 2, this.height - 28, 150, 20).build());
    }

    private OptionInstance<?> opt(String key, BaseXEnchantment e) {
        return new OptionInstance<>(
                key,
                OptionInstance.cachedConstantTooltip(Component.translatable(key)),
                (component, object) -> Component.literal(String.valueOf(e.getMaxLevel())),
                new OptionInstance.Enum<>(Collections.singletonList(e.getOptions()), null),
                null, null,
                c -> minecraft.setScreen(new OptionScreen(this, e.optGroup(), c)));
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
            EnchantUtil.allOptionsAfterReloading(ConfigUtil::write);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        renderDirtBackground(guiGraphics);

        super.render(guiGraphics, i, j, f);
    }
}
