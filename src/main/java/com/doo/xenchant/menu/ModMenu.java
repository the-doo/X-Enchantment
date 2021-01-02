package com.doo.xenchant.menu;

import com.doo.xenchant.menu.screen.ModMenuScreen;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<ModMenuScreen> getModConfigScreenFactory() {
        return ModMenuScreen::get;
    }
}
