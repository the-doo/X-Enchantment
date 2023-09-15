package com.doo.xenchantment.interfaces;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface WithOptions {

    String COMPATIBILITY_KEY = "compatibility";

    JsonObject getOptions();

    void loadOptions(JsonObject json);

    default ChatFormatting optionsTextColor() {
        return ChatFormatting.WHITE;
    }

    default void onOptionsRegister(BiConsumer<String, Supplier<Stream<String>>> register) {
        register.accept(COMPATIBILITY_KEY, () -> BuiltInRegistries.ENCHANTMENT.stream().map(Enchantment::getDescriptionId));
    }

    default double doubleV(String optionKey) {
        return getOptions().get(optionKey).getAsDouble();
    }

    default int intV(String optionKey) {
        return getOptions().get(optionKey).getAsInt();
    }

    default boolean boolV(String optionKey) {
        return getOptions().get(optionKey).getAsBoolean();
    }

    default void foreach(String optionKey, Consumer<JsonElement> callback) {
        JsonObject options = getOptions();
        if (!options.get(optionKey).isJsonArray()) {
            return;
        }

        options.getAsJsonArray(optionKey).forEach(callback);
    }

    default void loadIf(JsonObject json, String key) {
        JsonObject options = getOptions();
        Optional.ofNullable(json.get(key)).ifPresent(e -> {
            if (e.isJsonArray()) {
                options.add(key, e.getAsJsonArray());
                return;
            }

            try {
                options.addProperty(key, e.getAsDouble());
            } catch (Exception ex) {
                options.addProperty(key, e.getAsBoolean());
            }
        });
    }
}
