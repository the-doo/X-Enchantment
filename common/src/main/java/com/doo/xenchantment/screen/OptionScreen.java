package com.doo.xenchantment.screen;

import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.enchantment.BaseXEnchantment;
import com.doo.xenchantment.enchantment.halo.Halo;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
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

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class OptionScreen extends Screen {

    private static final Map<String, Supplier<Stream<String>>> MAP = Maps.newHashMap();

    public static void register(String title, String key, Supplier<Stream<String>> value) {
        MAP.put(getNameKey(title, key), value);
    }

    private Screen pre;
    private JsonObject options;
    private String title;

    private OptionsList list;

    public OptionScreen(Screen pre, String title, JsonObject options) {
        super(Component.empty());

        this.pre = pre;
        this.options = options;
        this.title = title;
    }

    @Override
    protected void init() {

        int w = this.width;
        list = new OptionsList(minecraft, w, this.height, 32, this.height - 32, 25);

        OptionInstance<?>[] instances = options.entrySet().stream().map(e -> {
            String nameKey = getNameKey(title, e.getKey());
            MutableComponent tooltip = Component.translatable(nameKey + ".tip");
            OptionInstance<?> opt;
            JsonElement json = e.getValue();
            if (json.isJsonArray()) {
                opt = OptionInstance.createBoolean(
                        nameKey,
                        OptionInstance.cachedConstantTooltip(tooltip),
                        MAP.containsKey(nameKey) && !json.getAsJsonArray().isEmpty(),
                        b -> minecraft.setScreen(ListScreen.get(json.getAsJsonArray(), MAP.get(nameKey), this)));
                return opt;
            }

            try {
                double value = json.getAsDouble();
                opt = new OptionInstance<>(
                        nameKey,
                        OptionInstance.cachedConstantTooltip(tooltip),
                        (component, d) -> Component.translatable(nameKey).append(": ").append(String.valueOf(d)),
                        new OptionInstance.IntRange(0, 200).xmap(i -> i * 0.5, d -> (int) (d * 2)),
                        Codec.doubleRange(0.5, 5),
                        value,
                        d -> {
                            if (minecraft.isLocalServer()) {
                                e.setValue(new JsonPrimitive(d));
                            }
                        });
            } catch (NumberFormatException ex) {
                opt = OptionInstance.createBoolean(nameKey, OptionInstance.cachedConstantTooltip(tooltip), json.getAsBoolean(), b -> {
                    if (minecraft.isLocalServer()) {
                        e.setValue(new JsonPrimitive(b));
                    }
                });
            }

            return opt;
        }).toArray(OptionInstance[]::new);

        list.addSmall(instances);

        addRenderableWidget(list);
        addRenderableWidget(new Button.Builder(CommonComponents.GUI_BACK, b -> close()).bounds(this.width / 2 - 150 / 2, this.height - 28, 150, 20).build());
    }

    private static String getNameKey(String title, String key) {
        String format = "%s.%s";
        String formatHalo = "%s.halo.%s";
        return switch (key) {
            case BaseXEnchantment.MAX_LEVEL_KEY ->
                    format.formatted(XEnchantment.MOD_ID, BaseXEnchantment.MAX_LEVEL_KEY);
            case BaseXEnchantment.COMPATIBILITY_KEY ->
                    format.formatted(XEnchantment.MOD_ID, BaseXEnchantment.COMPATIBILITY_KEY);
            case BaseXEnchantment.DISABLED_KEY -> format.formatted(XEnchantment.MOD_ID, BaseXEnchantment.DISABLED_KEY);
            case BaseXEnchantment.ONLY_ONE_LEVEL_KEY ->
                    format.formatted(XEnchantment.MOD_ID, BaseXEnchantment.ONLY_ONE_LEVEL_KEY);
            case Halo.RANGE_KEY -> formatHalo.formatted(XEnchantment.MOD_ID, Halo.RANGE_KEY);
            case Halo.INTERVAL_KEY -> formatHalo.formatted(XEnchantment.MOD_ID, Halo.INTERVAL_KEY);
            case Halo.PLAYER_ONLY_KEY -> formatHalo.formatted(XEnchantment.MOD_ID, Halo.PLAYER_ONLY_KEY);
            case Halo.HALO_KEY -> formatHalo.formatted(XEnchantment.MOD_ID, Halo.HALO_KEY);
            default -> format.formatted(title, key);
        };
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
