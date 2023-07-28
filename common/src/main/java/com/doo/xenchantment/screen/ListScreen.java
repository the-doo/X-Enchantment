package com.doo.xenchantment.screen;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class ListScreen extends Screen {

    private Screen pre;
    private JsonArray opts;
    private Supplier<Stream<String>> streams;

    private OptionsList list;

    private ListScreen(Screen pre, Supplier<Stream<String>> streams, JsonArray opts) {
        super(Component.empty());

        this.pre = pre;
        this.opts = opts;
        this.streams = streams;
    }

    public static ListScreen get(JsonArray array, Supplier<Stream<String>> streams, OptionScreen prev) {
        return new ListScreen(prev, streams, array);
    }

    @Override
    protected void init() {
        int w = this.width;
        list = new OptionsList(minecraft, w, this.height, 32, this.height - 32, 25);

        if (streams != null) {
            OptionInstance<?>[] instances = streams.get().map(e ->
                    OptionInstance.createBoolean(
                            e,
                            OptionInstance.noTooltip(),
                            opts.contains(new JsonPrimitive(e)),
                            b -> {
                                opts.remove(new JsonPrimitive(e));
                                if (Boolean.TRUE.equals(b)) {
                                    opts.add(e);
                                }
                            }
                    )).toArray(OptionInstance[]::new);

            list.addSmall(instances);
        }

        addRenderableWidget(list);
        addRenderableWidget(new Button.Builder(CommonComponents.GUI_BACK, b -> close()).bounds(this.width / 2 - 150 / 2, this.height - 28, 150, 20).build());
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
