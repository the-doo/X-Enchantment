package com.doo.xenchant;

import com.doo.xenchant.config.Config;
import com.doo.xenchant.config.Option;
import com.doo.xenchant.util.EnchantUtil;
import com.doo.xenchant.util.NetworkUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

import java.util.Set;

public class Enchant implements ModInitializer {

    public static final String ID = "x_enchant";

    public static final MinecraftClient MC = MinecraftClient.getInstance();

    public static Option option = new Option();

    @Override
    public void onInitialize() {
        // 读取配置
        option = Config.read(ID, Option.class, option);
        // 注册附魔
        EnchantUtil.registerAll();
        // 注册服务端事件
        NetworkUtil.registerAll();
    }
}
