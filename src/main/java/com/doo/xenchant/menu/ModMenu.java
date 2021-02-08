package com.doo.xenchant.menu;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.menu.screen.ModMenuScreen;
import com.google.common.collect.ImmutableMap;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import java.util.Map;

public class ModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<ModMenuScreen> getModConfigScreenFactory() {
        return ModMenuScreen::get;
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return ImmutableMap.of(Enchant.ID, getModConfigScreenFactory());
    }
}
