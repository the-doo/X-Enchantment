package com.doo.xenchant;

import com.doo.xenchant.config.Config;
import com.doo.xenchant.config.Option;
import com.doo.xenchant.util.EnchantUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Enchant implements ModInitializer {

    public static final String ID = "x_enchant";

    public static Option option = new Option();

    @Override
    public void onInitialize() {
        // has trinkets
        EnchantUtil.hasTrinkets = FabricLoader.getInstance().isModLoaded("trinkets");

        // 读取配置
        option = Config.read(ID, Option.class, option);

        // 注册附魔
        EnchantUtil.registerAll();
    }
}
