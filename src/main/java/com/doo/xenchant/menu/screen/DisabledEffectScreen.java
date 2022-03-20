package com.doo.xenchant.menu.screen;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.enchantment.halo.EffectHalo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.CycleOption;
import net.minecraft.client.Option;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Disabled status effect list screen
 */
public class DisabledEffectScreen extends Screen {

    private static final DisabledEffectScreen INSTANCE = new DisabledEffectScreen();

    private Screen pre;

    private OptionsList list;

    private DisabledEffectScreen() {
        super(new TextComponent(Enchant.ID));
    }

    public static DisabledEffectScreen get(Screen pre) {
        INSTANCE.width = pre.width;
        INSTANCE.height = pre.height;
        INSTANCE.pre = pre;
        return INSTANCE;
    }

    @Override
    protected void init() {
        list = new OptionsList(minecraft, this.width, this.height, 32, this.height - 32, 25);

        // All Button
        list.addBig(CycleOption.createOnOff("x_enchant.menu.option.status_effect_halo.enabled_all", ModMenuScreen.CLOSE, o -> Enchant.option.enabledAllEffect,
                (g, o, enabled) -> {
                    Enchant.option.enabledAllEffect = enabled;

                    // if enabled
                    if (enabled) {
                        // clear
                        Enchant.option.disabledEffect.clear();

                        // register all
                        Registry.MOB_EFFECT.stream()
                                .filter(e -> e != null && ResourceLocation.isValidResourceLocation(e.getDescriptionId()))
                                .forEach(EffectHalo::new);
                    } else {
                        // add all
                        Enchant.option.disabledEffect.addAll(Registry.MOB_EFFECT.stream()
                                .map(MobEffect::getDescriptionId)
                                .filter(ResourceLocation::isValidResourceLocation).collect(Collectors.toSet()));
                    }
                }));

        // only potion button
        list.addBig(CycleOption.createOnOff("x_enchant.menu.option.status_effect_halo.only_potion", ModMenuScreen.CLOSE, o -> Enchant.option.enabledAllEffect,
                (g, o, enabled) -> Enchant.option.onlyPotionEffect = enabled));

        // set options
        List<Option> total = Registry.MOB_EFFECT.stream()
                .filter(e -> ResourceLocation.isValidResourceLocation(e.getDescriptionId()))
                .map(e -> getButton(e, Enchant.option.disabledEffect)).collect(Collectors.toList());
        list.addSmall(total.toArray(new Option[]{}));


        this.addRenderableWidget(list);
        // 返回按钮
        this.addRenderableWidget(new Button(this.width / 2 - 150 / 2, this.height - 28, 150, 20, CommonComponents.GUI_BACK, b -> INSTANCE.close()));
    }

    private Option getButton(MobEffect effect, Collection<String> disabled) {
        String key = effect.getDescriptionId();
        return CycleOption.createOnOff(key, ModMenuScreen.CLOSE, o -> !disabled.contains(key), (g, o, enabled) -> {
            // if enabled
            if (enabled) {
                disabled.remove(key);

                new EffectHalo(effect);
            } else {
                disabled.add(key);
            }
        });
    }

    public void close() {
        if (minecraft != null) {
            minecraft.setScreen(this.pre);
        }
    }

    @Override
    public void render(@NotNull PoseStack matrices, int mouseX, int mouseY, float delta) {
        // 画背景
        super.renderBackground(matrices);
        // 画按钮
        list.render(matrices, mouseX, mouseY, delta);
        // 画其他
        super.render(matrices, mouseX, mouseY, delta);
    }
}
