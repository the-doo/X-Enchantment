package com.doo.xenchant.menu.screen;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.enchantment.halo.EffectHalo;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.client.option.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Disabled status effect list screen
 */
public class DisabledEffectScreen extends Screen {

    private static final DisabledEffectScreen INSTANCE = new DisabledEffectScreen();

    private Screen pre;

    private ButtonListWidget list;

    private DisabledEffectScreen() {
        super(new LiteralText(Enchant.ID));
    }

    public static DisabledEffectScreen get(Screen pre) {
        INSTANCE.width = pre.width;
        INSTANCE.height = pre.height;
        INSTANCE.pre = pre;
        return INSTANCE;
    }

    @Override
    protected void init() {
        list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);

        // All Button
        list.addSingleOptionEntry(CyclingOption.create("x_enchant.menu.option.status_effect_halo.enabled_all", ModMenuScreen.CLOSE, o -> Enchant.option.enabledAllEffect,
                (g, o, enabled) -> {
                    Enchant.option.enabledAllEffect = enabled;

                    // if enabled
                    if (enabled) {
                        // clear
                        Enchant.option.disabledEffect.clear();

                        // register all
                        Registry.STATUS_EFFECT.stream()
                                .filter(e -> e != null && Identifier.isValid(e.getTranslationKey()))
                                .forEach(EffectHalo::new);
                    } else {
                        // add all
                        Enchant.option.disabledEffect.addAll(Registry.STATUS_EFFECT.stream()
                                .map(StatusEffect::getTranslationKey)
                                .filter(Identifier::isValid).collect(Collectors.toSet()));
                    }
                }));

        // set options
        List<CyclingOption<Boolean>> total = Registry.STATUS_EFFECT.stream()
                .filter(e -> Identifier.isValid(e.getTranslationKey()))
                .map(e -> getButton(e, Enchant.option.disabledEffect)).toList();
        list.addAll(total.toArray(new Option[]{}));


        this.addSelectableChild(list);
        // 返回按钮
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 150 / 2, this.height - 28, 150, 20, ScreenTexts.BACK, b -> INSTANCE.close()));
    }

    private CyclingOption<Boolean> getButton(StatusEffect effect, Collection<String> disabled) {
        String key = effect.getTranslationKey();
        return CyclingOption.create(key, ModMenuScreen.CLOSE, o -> !disabled.contains(key), (g, o, enabled) -> {
            // if enabled
            if (enabled) {
                disabled.remove(key);

                new EffectHalo(effect);
            } else {
                disabled.add(key);
            }
        });
    }

    private void close() {
        if (client != null) {
            // 返回上个页面
            client.currentScreen = this.pre;
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
