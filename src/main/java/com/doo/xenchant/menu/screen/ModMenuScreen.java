package com.doo.xenchant.menu.screen;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.config.Config;
import com.doo.xenchant.enchantment.*;
import com.doo.xenchant.enchantment.halo.ThunderHalo;
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
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

/**
 * mod menu 配置界面
 */
public class ModMenuScreen extends Screen {

    public static final Text REOPEN = new TranslatableText("x_enchant.menu.option.open.tips");
    public static final Text CLOSE = new TranslatableText("x_enchant.menu.option.close.tips");

    private static final Option AUTO_FISHING = CyclingOption.create("x_enchant.menu.option.auto_fish", CLOSE,
            o -> Enchant.option.autoFishing, (g, o, d) -> {
                Enchant.option.autoFishing = d;

                if (d) {
                    Enchant.option.disabled.remove(AutoFish.class.getName());

                    new AutoFish();
                } else {
                    Enchant.option.disabled.add(AutoFish.class.getName());
                }
            });

    private static final Option SUCK_BLOOD = CyclingOption.create("x_enchant.menu.option.suck_blood", CLOSE,
            o -> Enchant.option.suckBlood, (g, o, d) -> {
                Enchant.option.suckBlood = d;

                if (d) {
                    Enchant.option.disabled.remove(SuckBlood.class.getName());

                    new SuckBlood();
                } else {
                    Enchant.option.disabled.add(SuckBlood.class.getName());
                }
            });

    private static final Option WEAKNESS = CyclingOption.create("x_enchant.menu.option.weakness", CLOSE,
            o -> Enchant.option.weakness, (g, o, d) -> {
                Enchant.option.weakness = d;

                if (d) {
                    Enchant.option.disabled.remove(Weakness.class.getName());

                    new Weakness();
                } else {
                    Enchant.option.disabled.add(Weakness.class.getName());
                }
            });

    private static final Option REBIRTH = CyclingOption.create("x_enchant.menu.option.rebirth", CLOSE,
            o -> Enchant.option.rebirth, (g, o, d) -> {
                Enchant.option.rebirth = d;

                if (d) {
                    Enchant.option.disabled.remove(Rebirth.class.getName());

                    new Rebirth();
                } else {
                    Enchant.option.disabled.add(Rebirth.class.getName());
                }
            });

    private static final Option MORE_LOOT = CyclingOption.create("x_enchant.menu.option.more_loot", CLOSE,
            o -> Enchant.option.moreLoot, (g, o, d) -> {
                Enchant.option.moreLoot = d;

                if (d) {
                    Enchant.option.disabled.remove(MoreLoot.class.getName());

                    new MoreLoot();
                } else {
                    Enchant.option.disabled.add(MoreLoot.class.getName());
                }
            });

    private static final Option MORE_LOOT_RATE = new DoubleOption("", 1, 100, 1,
            o -> Enchant.option.moreLootRate,
            (o, d) -> Enchant.option.moreLootRate = d,
            (g, o) -> new TranslatableText("x_enchant.menu.option.more_loot_rate", Enchant.option.moreLootRate));

    private static final Option MORE_MORE_LOOT_RATE = new DoubleOption("", 1, 100, 1,
            o -> Enchant.option.moreMoreLootRate,
            (o, d) -> Enchant.option.moreMoreLootRate = d,
            (g, o) -> new TranslatableText("x_enchant.menu.option.more_more_loot_rate", Enchant.option.moreMoreLootRate));

    private static final Option MORE_MORE_LOOT_MULTIPLIER = new DoubleOption("", 1, 100, 1,
            o -> Enchant.option.moreMoreLootMultiplier,
            (o, d) -> Enchant.option.moreMoreLootMultiplier = d,
            (g, o) -> new TranslatableText("x_enchant.menu.option.more_more_loot_multiplier", Enchant.option.moreMoreLootMultiplier));

    private static final Option HIT_RATE_UP = CyclingOption.create("x_enchant.menu.option.hit_rate_up", CLOSE,
            o -> Enchant.option.hitRateUp, (g, o, d) -> {
                Enchant.option.hitRateUp = d;

                if (d) {
                    Enchant.option.disabled.remove(HitRateUp.class.getName());

                    new HitRateUp();
                } else {
                    Enchant.option.disabled.add(HitRateUp.class.getName());
                }
            });

    private static final Option QUICK_SHOOT = CyclingOption.create("x_enchant.menu.option.quick_shot", CLOSE,
            o -> Enchant.option.quickShot, (g, o, d) -> {
                Enchant.option.quickShot = d;

                if (d) {
                    Enchant.option.disabled.remove(QuickShot.class.getName());

                    new QuickShot();
                } else {
                    Enchant.option.disabled.add(QuickShot.class.getName());
                }
            });

    private static final Option MAGIC_IMMUNE = CyclingOption.create("x_enchant.menu.option.magic_immune", CLOSE,
            o -> Enchant.option.magicImmune, (g, o, d) -> {
                Enchant.option.magicImmune = d;

                if (d) {
                    Enchant.option.disabled.remove(MagicImmune.class.getName());

                    new MagicImmune();
                } else {
                    Enchant.option.disabled.add(MagicImmune.class.getName());
                }
            });

    private static final Option DIFFUSION = CyclingOption.create("x_enchant.menu.option.diffusion", CLOSE,
            o -> Enchant.option.diffusion, (g, o, d) -> {
                Enchant.option.diffusion = d;

                if (d) {
                    Enchant.option.disabled.remove(Diffusion.class.getName());

                    new Diffusion();
                } else {
                    Enchant.option.disabled.add(Diffusion.class.getName());
                }
            });

    private static final Option DIFFUSION_DAMAGE = new DoubleOption("", 1, 40, 0.5F,
            o -> Enchant.option.diffusionDamage,
            (o, d) -> Enchant.option.diffusionDamage = d,
            (g, o) -> new TranslatableText("x_enchant.menu.option.diffusion.damage", Enchant.option.diffusionDamage));

    private static final Option NIGHT_BREAK_PER_LEVEL = new DoubleOption("", 0, 100, 1F,
            o -> Enchant.option.nightBreakPerLevel,
            (o, d) -> Enchant.option.nightBreakPerLevel = d,
            (g, o) -> new TranslatableText("x_enchant.menu.option.night_break.per_level", Enchant.option.nightBreakPerLevel));

    private static final Option NIGHT_BREAK_MAX_LEVEL = new DoubleOption("", 1, 5, 1F,
            o -> (double) Enchant.option.nightBreakMaxLevel,
            (o, d) -> Enchant.option.nightBreakMaxLevel = d.intValue(),
            (g, o) -> new TranslatableText("x_enchant.menu.option.night_break.max_level", Enchant.option.nightBreakMaxLevel));

    private static final Option BROKEN_DAWN = CyclingOption.create("x_enchant.menu.option.broken_dawn", CLOSE,
            o -> Enchant.option.brokenDawn, (g, o, d) -> {
                Enchant.option.brokenDawn = d;

                if (d) {
                    Enchant.option.disabled.remove(BrokenDawn.class.getName());

                    new BrokenDawn();
                } else {
                    Enchant.option.disabled.add(BrokenDawn.class.getName());
                }
            });

    private static final Option BROKEN_DAWN_PROCESS = new DoubleOption("", 0.5, 10, 0.5F,
            o -> Enchant.option.brokenDawnProcess,
            (o, d) -> Enchant.option.brokenDawnProcess = d,
            (g, o) -> new TranslatableText("x_enchant.menu.option.broken_dawn.process", Enchant.option.brokenDawnProcess));

    private static final Option BROKEN_DAWN_SUCCESS = new DoubleOption("", 0, 100, 1F,
            o -> Enchant.option.brokenDawnSuccess,
            (o, d) -> Enchant.option.brokenDawnSuccess = d,
            (g, o) -> new TranslatableText("x_enchant.menu.option.broken_dawn.success", Enchant.option.brokenDawnSuccess));

    private static final Option SPECIAL = CyclingOption.create("x_enchant.menu.option.enchantment.special",
            o -> Enchant.option.special, (g, o, d) -> Enchant.option.special = d);

    private static final Option TRINKETS = CyclingOption.create("x_enchant.menu.option.enchantment.trinkets",
            o -> Enchant.option.trinkets, (g, o, d) -> Enchant.option.trinkets = d);


    private static final Option HALO =
            CyclingOption.create("x_enchant.menu.option.enchantment.halo", REOPEN,
                    o -> Enchant.option.halo, (g, o, d) -> Enchant.option.halo = d);

    private static final Option HALO_RANGE = new DoubleOption("", 1, 20, 1,
            o -> Enchant.option.haloRange,
            (o, d) -> Enchant.option.haloRange = d,
            (g, o) -> new TranslatableText("x_enchant.menu.option.halo_range", Enchant.option.haloRange));

    private static final Option HARMFUL_TARGET_ONLY_MONSTER = CyclingOption.create("x_enchant.menu.option.harmful_target_only_monster",
            o -> Enchant.option.harmfulTargetOnlyMonster, (g, o, d) -> Enchant.option.harmfulTargetOnlyMonster = d);

    private static final Option HALO_TARGET = CyclingOption.create("x_enchant.menu.option.halo_allow_target", com.doo.xenchant.config.Option.AllowTarget.values(), t -> t.key,
            g -> Enchant.option.haloAllowOther, (g, O, v) -> Enchant.option.haloAllowOther = v);

    private static final Option THUNDER_HALO = CyclingOption.create("x_enchant.menu.option.thunder_halo", REOPEN,
            o -> Enchant.option.thunderHalo, (g, o, d) -> {
                Enchant.option.thunderHalo = d;

                if (d) {
                    Enchant.option.disabled.remove(ThunderHalo.class.getName());

                    new ThunderHalo();
                } else {
                    Enchant.option.disabled.add(ThunderHalo.class.getName());
                }
            });

    private static final Option THUNDER_HALO_TARGET = CyclingOption.create("x_enchant.menu.option.thunder_halo_allow_target", com.doo.xenchant.config.Option.AllowTarget.values(), t -> t.key,
            g -> Enchant.option.thunderHaloAllowOther, (g, O, v) -> Enchant.option.thunderHaloAllowOther = v);

    private static final Option THUNDER_HALO_TREASURE = CyclingOption.create("x_enchant.menu.option.thunder_halo_treasure",
            o -> Enchant.option.thunderHaloIsTreasure, (g, o, d) -> Enchant.option.thunderHaloIsTreasure = d);

    private static final Option THUNDER_HALO_CHANCE = new DoubleOption("", 1, 100, 1,
            o -> (double) Enchant.option.thunderHaloStruckChance,
            (o, d) -> Enchant.option.thunderHaloStruckChance = d.intValue(),
            (g, o) -> new TranslatableText("x_enchant.menu.option.thunder_halo_chance", Enchant.option.thunderHaloStruckChance));

    private static final Option TREASURE_EFFECT_LEVEL = new DoubleOption("", 1, 10, 1,
            o -> (double) Enchant.option.effectTreasureMaxLevel,
            (o, d) -> Enchant.option.effectTreasureMaxLevel = d.intValue(),
            (g, o) -> new TranslatableText("x_enchant.menu.option.halo_effect_level_treasure", Enchant.option.effectTreasureMaxLevel));

    private static final Option OTHER_EFFECT_LEVEL = new DoubleOption("", 1, 10, 1,
            o -> (double) Enchant.option.effectOtherMaxLevel,
            (o, d) -> Enchant.option.effectOtherMaxLevel = d.intValue(),
            (g, o) -> new TranslatableText("x_enchant.menu.option.halo_effect_level_other", Enchant.option.effectOtherMaxLevel));

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
            HALO, HALO_RANGE,
            HALO_TARGET, HARMFUL_TARGET_ONLY_MONSTER,
            THUNDER_HALO, THUNDER_HALO_TARGET,
            THUNDER_HALO_TREASURE, THUNDER_HALO_CHANCE,
            TREASURE_EFFECT_LEVEL, OTHER_EFFECT_LEVEL,
            STATUS_EFFECT
    };
    private static final Option[] ENCHANT_OPTION = {
            AUTO_FISHING, SUCK_BLOOD,
            WEAKNESS, REBIRTH,
            MORE_LOOT, MORE_LOOT_RATE,
            MORE_MORE_LOOT_RATE, MORE_MORE_LOOT_MULTIPLIER,
            HIT_RATE_UP, QUICK_SHOOT,
            MAGIC_IMMUNE,
            DIFFUSION, DIFFUSION_DAMAGE,
            NIGHT_BREAK_PER_LEVEL, NIGHT_BREAK_MAX_LEVEL,
            BROKEN_DAWN, BROKEN_DAWN_PROCESS,
            BROKEN_DAWN_SUCCESS
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
        list.addOptionEntry(SPECIAL, TRINKETS);
        this.addSelectableChild(list);
        // 返回按钮
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 150 / 2, this.height - 28, 150, 20,
                ScreenTexts.BACK, b -> INSTANCE.close()));
    }

    public void close() {
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
