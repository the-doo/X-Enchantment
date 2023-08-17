package com.doo.xenchantment.fabric;

import com.doo.playerinfo.utils.ExtractAttributes;
import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.util.EnchantUtil;
import com.doo.xenchantment.util.ServersideChannelUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.ModifyItemAttributeModifiersCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class XEnchantmentFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        XEnchantment.init(() -> ExtractAttributes.ATTACK_RANGE);

        EnchantUtil.registerAll(e -> Registry.register(BuiltInRegistries.ENCHANTMENT, e.getId(), e));
        EnchantUtil.registerAttr(e -> ModifyItemAttributeModifiersCallback.EVENT.register((stack, slot, attributeModifiers) ->
                e.insertAttr(stack, slot, (a, m) -> attributeModifiers.get(a).add(m))));
        EnchantUtil.registerAdv(
                e -> Optional.ofNullable(e.getAdvTrigger()).ifPresent(CriteriaTriggers::register));

        ServersideChannelUtil.setSender((p, i, b, o) -> ServerPlayNetworking.send(p, i, b));

        ServerLifecycleEvents.SERVER_STARTING.register(EnchantUtil::onServer);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> EnchantUtil.onServerStarted());

        EnchantUtil.onKilled(e -> ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killed) -> {
            if (entity instanceof LivingEntity living) {
                e.onKilled(world, living, killed);
            }
        }));

        EnchantUtil.canDeath(e -> ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> e.canDeath(entity)));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                ServersideChannelUtil.send(handler.player, EnchantUtil.CONFIG_CHANNEL, ServersideChannelUtil.getJsonBuf(EnchantUtil.allOptions()), EnchantUtil.allOptions()));
    }
}