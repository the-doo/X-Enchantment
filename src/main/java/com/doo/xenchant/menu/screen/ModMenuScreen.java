package com.doo.xenchant.menu.screen;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.config.Config;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

/**
 * mod menu 配置界面
 */
public class ModMenuScreen extends Screen {

    private static final CyclingOption<Boolean> AUTO_FISHING = CyclingOption.create("x_enchant.menu.option.auto_fish",
            o -> Enchant.option.autoFishing, (g, o, d) -> Enchant.option.autoFishing = d);

    private static final CyclingOption<Boolean> SUCK_BLOOD = CyclingOption.create("x_enchant.menu.option.suck_blood",
            o -> Enchant.option.suckBlood, (g, o, d) -> Enchant.option.suckBlood = d);

    private static final CyclingOption<Boolean> WEAKNESS = CyclingOption.create("x_enchant.menu.option.weakness",
            o -> Enchant.option.weakness, (g, o, d) -> Enchant.option.weakness = d);

    private static final CyclingOption<Boolean> REBIRTH = CyclingOption.create("x_enchant.menu.option.rebirth",
            o -> Enchant.option.rebirth, (g, o, d) -> Enchant.option.rebirth = d);

    private static final CyclingOption<Boolean> MORE_LOOT = CyclingOption.create("x_enchant.menu.option.more_loot",
            o -> Enchant.option.moreLoot, (g, o, d) -> Enchant.option.moreLoot = d);

    private static final DoubleOption MORE_LOOT_RATE = new DoubleOption("", 1, 100, 1,
            o -> Enchant.option.moreLootRate,
            (o, d) -> Enchant.option.moreLootRate = d,
            (g, o) -> new TranslatableText("x_enchant.menu.option.more_loot_rate", Enchant.option.moreLootRate));

    private static final DoubleOption MORE_MORE_LOOT_RATE = new DoubleOption("", 1, 100, 1,
            o -> Enchant.option.moreMoreLootRate,
            (o, d) -> Enchant.option.moreMoreLootRate = d,
            (g, o) -> new TranslatableText("x_enchant.menu.option.more_more_loot_rate", Enchant.option.moreMoreLootRate));

    private static final DoubleOption MORE_MORE_LOOT_MULTIPLIER = new DoubleOption("", 1, 100, 1,
            o -> Enchant.option.moreMoreLootMultiplier,
            (o, d) -> Enchant.option.moreMoreLootMultiplier = d,
            (g, o) -> new TranslatableText("x_enchant.menu.option.more_more_loot_multiplier", Enchant.option.moreMoreLootMultiplier));

    private static final CyclingOption<Boolean> INFINITY_ACCEPT_MENDING = CyclingOption.create("x_enchant.menu.option.infinity_accept_mending",
            o -> Enchant.option.infinityAcceptMending, (g, o, d) -> Enchant.option.infinityAcceptMending = d);

    private static final CyclingOption<Boolean> HIT_RATE_UP = CyclingOption.create("x_enchant.menu.option.hit_rate_up",
            o -> Enchant.option.hitRateUp, (g, o, d) -> Enchant.option.hitRateUp = d);

    private static final CyclingOption<Boolean> QUICK_SHOOT = CyclingOption.create("x_enchant.menu.option.quick_shoot",
            o -> Enchant.option.quickShoot, (g, o, d) -> Enchant.option.quickShoot = d);

    private static final CyclingOption<Boolean> MAGIC_IMMUNE = CyclingOption.create("x_enchant.menu.option.magic_immune",
            o -> Enchant.option.magicImmune, (g, o, d) -> Enchant.option.magicImmune = d);

    private static final CyclingOption<Boolean> CHAT_TIPS = CyclingOption.create("x_enchant.menu.option.chat_tips",
            o -> Enchant.option.chatTips, (g, o, d) -> Enchant.option.chatTips = d);

    private static final CyclingOption<Boolean> HALO = CyclingOption.create("x_enchant.menu.option.halo",
            o -> Enchant.option.halo, (g, o, d) -> Enchant.option.halo = d);

    private static final DoubleOption HALO_RANGE = new DoubleOption("", 1, 20, 1,
            o -> Enchant.option.haloRange,
            (o, d) -> Enchant.option.haloRange = d,
            (g, o) -> new TranslatableText("x_enchant.menu.option.halo_range", Enchant.option.haloRange));

    private static final CyclingOption<Boolean> THUNDER_HALO = CyclingOption.create("x_enchant.menu.option.thunder_halo",
            o -> Enchant.option.thunderHalo, (g, o, d) -> Enchant.option.thunderHalo = d);

    private static final CyclingOption<Boolean> THUNDER_HALO_TREASURE = CyclingOption.create("x_enchant.menu.option.thunder_halo_treasure",
            o -> Enchant.option.thunderHaloIsTreasure, (g, o, d) -> Enchant.option.thunderHaloIsTreasure = d);

    private static final DoubleOption THUNDER_HALO_CHANCE = new DoubleOption("", 1, 100, 1,
            o -> (double) Enchant.option.thunderHaloStruckChance,
            (o, d) -> Enchant.option.thunderHaloStruckChance = d.intValue(),
            (g, o) -> new TranslatableText("x_enchant.menu.option.thunder_halo_chance", Enchant.option.thunderHaloStruckChance));

    private static final Option STATUS_EFFECT = new Option("x_enchant.menu.option.status_effect_halo") {
        @Override
        public ClickableWidget createButton(GameOptions options, int x, int y, int width) {
            return new ButtonWidget(x, y, width, 20, getDisplayPrefix(), b -> {
                if (INSTANCE.client != null) {
                    INSTANCE.client.setScreen(DisabledEffectScreen.get(INSTANCE));
                }
            });
        }
    };
    private static final Option[] HALO_OPTION = {
            HALO, HALO_RANGE, THUNDER_HALO, THUNDER_HALO_TREASURE, THUNDER_HALO_CHANCE,
            STATUS_EFFECT
    };
    private static final Option[] ENCHANT_OPTION = {
            AUTO_FISHING, SUCK_BLOOD, WEAKNESS, REBIRTH,
            MORE_LOOT, MORE_LOOT_RATE, MORE_MORE_LOOT_RATE, MORE_MORE_LOOT_MULTIPLIER,
            INFINITY_ACCEPT_MENDING, HIT_RATE_UP, QUICK_SHOOT, MAGIC_IMMUNE, CHAT_TIPS,
    };

    private static final ModMenuScreen INSTANCE = new ModMenuScreen();

    private Screen pre;

    private ButtonListWidget list;

    private ModMenuScreen() {
        super(new LiteralText(Enchant.ID));
    }

    public static ModMenuScreen get(Screen pre) {
        INSTANCE.width = pre.width;
        INSTANCE.height = pre.height;
        INSTANCE.pre = pre;
        return INSTANCE;
    }

    @Override
    protected void init() {
        list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
        // 显示基础高度
        list.addAll(ENCHANT_OPTION);
        list.addAll(HALO_OPTION);
        this.addSelectableChild(list);
        // 返回按钮
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 150 / 2, this.height - 28, 150, 20,
                ScreenTexts.BACK, b -> INSTANCE.close()));
    }

    private void close() {
        if (client != null) {
            // 返回上个页面
            client.currentScreen = this.pre;
            // 保存设置的配置
            Config.write(Enchant.ID, Enchant.option);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // 画背景
        super.renderBackground(matrices);
        // 画按钮
        list.render(matrices, mouseX, mouseY, delta);
        // 画其他
        super.render(matrices, mouseX, mouseY, delta);
    }
}
