package com.doo.xenchantment.menu;

import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.screen.MenuScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import java.util.Map;

public class ModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return MenuScreen::get;
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return Map.of(XEnchantment.MOD_ID, getModConfigScreenFactory());
    }
}
