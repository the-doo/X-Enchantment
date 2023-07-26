package com.doo.xenchantment.screen;

import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.enchantment.BaseXEnchantment;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class OptionScreen extends Screen {

    private Screen pre;
    private JsonObject options;
    private String key;

    private OptionsList list;

    public OptionScreen(Screen pre, String title, JsonObject options) {
        super(Component.empty());

        this.pre = pre;
        this.options = options;
        this.key = String.format("%s.menu.option.%s", XEnchantment.MOD_ID, title);
    }

    @Override
    protected void init() {

        int w = this.width;
        list = new OptionsList(minecraft, w, this.height, 32, this.height - 32, 25);

        OptionInstance<?>[] instances = options.entrySet().stream().map(e -> {
            String nameKey = getNameKey(e.getKey());
            MutableComponent tooltip = Component.translatable(nameKey + ".tip");
            OptionInstance<?> opt;
            try {
                double value = e.getValue().getAsDouble();
                opt = new OptionInstance<>(
                        nameKey,
                        OptionInstance.cachedConstantTooltip(tooltip),
                        (component, d) -> Component.translatable(nameKey).append(": ").append(String.valueOf(d)),
                        new OptionInstance.IntRange(0, 200).xmap(i -> i * 0.5, d -> (int) (d * 2)),
                        Codec.doubleRange(0.5, 5),
                        value,
                        d -> e.setValue(new JsonPrimitive(d)));
            } catch (NumberFormatException ex) {
                opt = OptionInstance.createBoolean(nameKey, OptionInstance.cachedConstantTooltip(tooltip), e.getValue().getAsBoolean(), b -> e.setValue(new JsonPrimitive(b)));
            }

            return opt;
        }).toArray(OptionInstance[]::new);

        list.addSmall(instances);

        addRenderableWidget(list);
        addRenderableWidget(new Button.Builder(CommonComponents.GUI_BACK, b -> close()).bounds(this.width / 2 - 150 / 2, this.height - 28, 150, 20).build());
    }

    private String getNameKey(String key) {
        if (key.equals(BaseXEnchantment.MAX_LEVEL_KEY)) {
            return XEnchantment.MOD_ID + "." + BaseXEnchantment.MAX_LEVEL_KEY;
        }
        if (key.equals(BaseXEnchantment.DISABLED_KEY)) {
            return XEnchantment.MOD_ID + "." + BaseXEnchantment.DISABLED_KEY;
        }
        if (key.equals(BaseXEnchantment.ONLY_ONE_LEVEL_KEY)) {
            return XEnchantment.MOD_ID + "." + BaseXEnchantment.ONLY_ONE_LEVEL_KEY;
        }

        return this.key + "." + key;
    }

    public void close() {
        minecraft.setScreen(pre);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        renderDirtBackground(guiGraphics);

        super.render(guiGraphics, i, j, f);
    }
}
