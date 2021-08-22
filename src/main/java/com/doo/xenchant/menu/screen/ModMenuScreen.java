package com.doo.xenchant.menu.screen;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.config.Config;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

/**
 * mod menu 配置界面
 */
public class ModMenuScreen extends Screen {

    private static final BooleanOption AUTO_FISHING = new BooleanOption("x_enchant.menu.option.auto_fish",
            o -> Enchant.option.autoFishing, (o, d) -> Enchant.option.autoFishing = d);

    private static final BooleanOption SUCK_BLOOD = new BooleanOption("x_enchant.menu.option.suck_blood",
            o -> Enchant.option.suckBlood, (o, d) -> Enchant.option.suckBlood = d);

    private static final BooleanOption WEAKNESS = new BooleanOption("x_enchant.menu.option.weakness",
            o -> Enchant.option.weakness, (o, d) -> Enchant.option.weakness = d);

    private static final BooleanOption REBIRTH = new BooleanOption("x_enchant.menu.option.rebirth",
            o -> Enchant.option.rebirth, (o, d) -> Enchant.option.rebirth = d);

    private static final BooleanOption MORE_LOOT = new BooleanOption("x_enchant.menu.option.more_loot",
            o -> Enchant.option.moreLoot, (o, d) -> Enchant.option.moreLoot = d);

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

    private static final BooleanOption INFINITY_ACCEPT_MENDING = new BooleanOption("x_enchant.menu.option.infinity_accept_mending",
            o -> Enchant.option.infinityAcceptMending, (o, d) -> Enchant.option.infinityAcceptMending = d);

    private static final BooleanOption HIT_RATE_UP = new BooleanOption("x_enchant.menu.option.hit_rate_up",
            o -> Enchant.option.hitRateUp, (o, d) -> Enchant.option.hitRateUp = d);

    private static final BooleanOption QUICK_SHOOT = new BooleanOption("x_enchant.menu.option.quick_shoot",
            o -> Enchant.option.quickShoot, (o, d) -> Enchant.option.quickShoot = d);

    private static final BooleanOption MAGIC_IMMUNE = new BooleanOption("x_enchant.menu.option.magic_immune",
            o -> Enchant.option.magicImmune, (o, d) -> Enchant.option.magicImmune = d);

    private static final BooleanOption CHAT_TIPS = new BooleanOption("x_enchant.menu.option.chat_tips",
            o -> Enchant.option.chatTips, (o, d) -> Enchant.option.chatTips = d);

    private static final BooleanOption HALO = new BooleanOption("x_enchant.menu.option.halo",
            o -> Enchant.option.halo, (o, d) -> Enchant.option.halo = d);

    private static final DoubleOption HALO_RANGE = new DoubleOption("", 1, 20, 1,
            o -> Enchant.option.haloRange,
            (o, d) -> Enchant.option.haloRange = d,
            (g, o) -> new TranslatableText("x_enchant.menu.option.halo_range", Enchant.option.haloRange));

    private static final DoubleOption HALO_TICK = new DoubleOption("", 1, 100, 1,
            o -> Enchant.option.haloInterval,
            (o, d) -> Enchant.option.haloInterval = d,
            (g, o) -> new TranslatableText("x_enchant.menu.option.halo_interval", Enchant.option.haloInterval));

    private static final BooleanOption ATTACK_SPEED_HALO = new BooleanOption("x_enchant.menu.option.attack_speed_halo",
            o -> Enchant.option.attackSpeedHalo, (o, d) -> Enchant.option.attackSpeedHalo = d);

    private static final DoubleOption ATTACK_SPEED_HALO_MULTI = new DoubleOption("", 1, 10, 1,
            o -> Enchant.option.attackSpeedHaloMultiple,
            (o, d) -> Enchant.option.attackSpeedHaloMultiple = d,
            (g, o) -> new TranslatableText("x_enchant.menu.option.attack_speed_halo_multi", Enchant.option.attackSpeedHaloMultiple));

    private static final BooleanOption LUCK_HALO = new BooleanOption("x_enchant.menu.option.luck_halo",
            o -> Enchant.option.luckHalo, (o, d) -> Enchant.option.luckHalo = d);

    private static final BooleanOption LUCK_HALO_TREASURE = new BooleanOption("x_enchant.menu.option.luck_halo_treasure",
            o -> Enchant.option.luckHaloIsTreasure, (o, d) -> Enchant.option.luckHaloIsTreasure = d);

    private static final DoubleOption LUCK_HALO_DURATION = new DoubleOption("", 1, 60, 1,
            o -> (double) Enchant.option.luckHaloDuration,
            (o, d) -> Enchant.option.luckHaloDuration = d.intValue(),
            (g, o) -> new TranslatableText("x_enchant.menu.option.luck_halo_duration", Enchant.option.luckHaloDuration));

    private static final DoubleOption LUCK_HALO_LEVEL = new DoubleOption("", 1, 10, 1,
            o -> (double) Enchant.option.luckHaloLevel,
            (o, d) -> Enchant.option.luckHaloLevel = d.intValue(),
            (g, o) -> new TranslatableText("x_enchant.menu.option.luck_halo_level", Enchant.option.luckHaloLevel));

    private static final BooleanOption MAX_HP_HALO = new BooleanOption("x_enchant.menu.option.max_hp_halo",
            o -> Enchant.option.maxHPHalo, (o, d) -> Enchant.option.maxHPHalo = d);

    private static final DoubleOption MAX_HP_HALO_MULTI = new DoubleOption("", 1, 10, 1,
            o -> Enchant.option.maxHPHaloMultiple,
            (o, d) -> Enchant.option.maxHPHaloMultiple = d,
            (g, o) -> new TranslatableText("x_enchant.menu.option.max_hp_halo_multi", Enchant.option.maxHPHaloMultiple));

    private static final BooleanOption REGENERATION_HALO = new BooleanOption("x_enchant.menu.option.regeneration_halo",
            o -> Enchant.option.regenerationHalo, (o, d) -> Enchant.option.regenerationHalo = d);

    private static final DoubleOption REGENERATION_HALO_DURATION = new DoubleOption("", 1, 60, 1,
            o -> (double) Enchant.option.regenerationHaloDuration,
            (o, d) -> Enchant.option.regenerationHaloDuration = d.intValue(),
            (g, o) -> new TranslatableText("x_enchant.menu.option.regeneration_halo_duration", Enchant.option.regenerationHaloDuration));

    private static final DoubleOption REGENERATION_HALO_LEVEL = new DoubleOption("", 1, 10, 1,
            o -> (double) Enchant.option.regenerationHaloLevel,
            (o, d) -> Enchant.option.regenerationHaloLevel = d.intValue(),
            (g, o) -> new TranslatableText("x_enchant.menu.option.regeneration_halo_level", Enchant.option.regenerationHaloLevel));

    private static final BooleanOption SLOWNESS_HALO = new BooleanOption("x_enchant.menu.option.slowness_halo",
            o -> Enchant.option.slownessHalo, (o, d) -> Enchant.option.slownessHalo = d);

    private static final DoubleOption SLOWNESS_HALO_DURATION = new DoubleOption("", 1, 60, 1,
            o -> (double) Enchant.option.slownessHaloDuration,
            (o, d) -> Enchant.option.slownessHaloDuration = d.intValue(),
            (g, o) -> new TranslatableText("x_enchant.menu.option.slowness_halo_duration", Enchant.option.slownessHaloDuration));

    private static final DoubleOption SLOWNESS_HALO_LEVEL = new DoubleOption("", 1, 10, 1,
            o -> (double) Enchant.option.slownessHaloLevel,
            (o, d) -> Enchant.option.slownessHaloLevel = d.intValue(),
            (g, o) -> new TranslatableText("x_enchant.menu.option.slowness_halo_level", Enchant.option.slownessHaloLevel));

    private static final BooleanOption THUNDER_HALO = new BooleanOption("x_enchant.menu.option.thunder_halo",
            o -> Enchant.option.thunderHalo, (o, d) -> Enchant.option.thunderHalo = d);

    private static final BooleanOption THUNDER_HALO_TREASURE = new BooleanOption("x_enchant.menu.option.thunder_halo_treasure",
            o -> Enchant.option.thunderHaloIsTreasure, (o, d) -> Enchant.option.thunderHaloIsTreasure = d);

    private static final DoubleOption THUNDER_HALO_CHANCE = new DoubleOption("", 1, 100, 1,
            o -> (double) Enchant.option.thunderHaloStruckChance,
            (o, d) -> Enchant.option.thunderHaloStruckChance = d.intValue(),
            (g, o) -> new TranslatableText("x_enchant.menu.option.thunder_halo_chance", Enchant.option.thunderHaloStruckChance));

    private static final ModMenuScreen INSTANCE = new ModMenuScreen();

    private Screen pre;

    private ButtonListWidget list;

    private ModMenuScreen() {
        super(new LiteralText(Enchant.ID));
    }

    @Override
    protected void init() {
        list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
        Option[] options = {
                AUTO_FISHING, SUCK_BLOOD, WEAKNESS, REBIRTH, MORE_LOOT, MORE_LOOT_RATE, MORE_MORE_LOOT_RATE, MORE_MORE_LOOT_MULTIPLIER, INFINITY_ACCEPT_MENDING,
                HIT_RATE_UP, QUICK_SHOOT, MAGIC_IMMUNE, CHAT_TIPS, HALO,
                HALO_RANGE, HALO_TICK,
                ATTACK_SPEED_HALO, ATTACK_SPEED_HALO_MULTI,
                LUCK_HALO, LUCK_HALO_TREASURE, LUCK_HALO_DURATION, LUCK_HALO_LEVEL,
                MAX_HP_HALO, MAX_HP_HALO_MULTI,
                REGENERATION_HALO, REGENERATION_HALO_DURATION, REGENERATION_HALO_LEVEL,
                SLOWNESS_HALO, SLOWNESS_HALO_DURATION, SLOWNESS_HALO_LEVEL,
                THUNDER_HALO, THUNDER_HALO_TREASURE, THUNDER_HALO_CHANCE
        };
        // 显示基础高度
        list.addAll(options);
        this.addChild(list);
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
