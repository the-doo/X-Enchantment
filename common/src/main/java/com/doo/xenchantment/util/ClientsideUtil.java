package com.doo.xenchantment.util;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class ClientsideUtil {

    private ClientsideUtil() {
    }

    private static Supplier<Object> minecraftGetter = () -> null;
    private static Supplier<Object> localPlayerGetter = () -> null;

    public static <T> T minecraft() {
        return (T) minecraftGetter.get();
    }

    public static <T> T player() {
        return (T) localPlayerGetter.get();
    }

    public static <T> void setMinecraft(Supplier<T> getter) {
        minecraftGetter = (Supplier<Object>) getter;
    }

    public static <T> void setLocalPlayerGetter(Supplier<T> getter) {
        localPlayerGetter = (Supplier<Object>) getter;
    }
}
