package com.doo.xenchant;

import com.doo.xenchant.config.Config;
import com.doo.xenchant.config.Option;
import com.doo.xenchant.util.EnchantUtil;
import net.fabricmc.api.ModInitializer;

public class Enchant implements ModInitializer {

	public static final String ID = "xenchant";

	public static Option option = new Option();

	@Override
	public void onInitialize() {
		// 读取配置
		option = Config.read(ID, Option.class, option);
		// 注册附魔
		EnchantUtil.registerAll();
	}
}
