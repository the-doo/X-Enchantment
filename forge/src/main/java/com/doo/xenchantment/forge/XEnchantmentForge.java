package com.doo.xenchantment.forge;

import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.forge.mixin.accessor.CanBurnAccessor;
import com.doo.xenchantment.screen.MenuScreen;
import com.doo.xenchantment.util.ClientsideChannelUtil;
import com.doo.xenchantment.util.ClientsideUtil;
import com.doo.xenchantment.util.EnchantUtil;
import com.doo.xenchantment.util.ServersideChannelUtil;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

@Mod(XEnchantment.MOD_ID)
public class XEnchantmentForge {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(XEnchantment.MOD_ID, "xenchantment_pack_sender"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public XEnchantmentForge() {
        XEnchantment.init();

        XEnchantment.setAttrGetter(ForgeMod.ENTITY_REACH);
        XEnchantment.setCanBurnGetter((o, registryAccess, recipe, nonNullList, i) ->
                ((CanBurnAccessor) o).invokeCanBurn(registryAccess, recipe, nonNullList, i));

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::register);
        modEventBus.addListener(this::onSetup);

        INSTANCE.registerMessage(0, String.class, (a, m) -> {
        }, b -> "", ((packet, contextSupplier) -> {
            contextSupplier.get().enqueueWork(ClientsideChannelUtil::autoFish);
            contextSupplier.get().setPacketHandled(true);
        }));

        INSTANCE.registerMessage(1, JsonObject.class,
                ServersideChannelUtil::getJsonBuf,
                ServersideChannelUtil::getConfig,
                ((packet, contextSupplier) -> {
                    contextSupplier.get().enqueueWork(() -> ClientsideChannelUtil.loadConfig(packet));
                    contextSupplier.get().setPacketHandled(true);
                }));


        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void register(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.ENCHANTMENTS, helper -> EnchantUtil.registerAll(e -> helper.register(e.getId(), e)));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onAttrMaps(ItemAttributeModifierEvent event) {
        EnchantUtil.registerAttr(e -> e.insertAttr(event.getItemStack(), event.getSlotType(), event::addModifier));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() ->
                EnchantUtil.registerAdv(e -> Optional.ofNullable(e.getAdvTrigger()).ifPresent(CriteriaTriggers::register)));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void canDeath(LivingDeathEvent event) {
        EnchantUtil.canDeath(e -> {
            if (!event.isCanceled() && !e.canDeath(event.getEntity())) {
                event.setCanceled(true);
            }
        });

        if (event.getEntity().level().isClientSide() || event.isCanceled()) {
            return;
        }

        if (event.getSource().getEntity() instanceof LivingEntity living) {
            EnchantUtil.onKilled(e -> e.onKilled((ServerLevel) event.getEntity().level(), living, event.getEntity()));
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), EnchantUtil.allOptions());
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        ServersideChannelUtil.setSender((player, id, buf, json) ->
                INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), json));

        EnchantUtil.onServer(event.getServer());
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        EnchantUtil.onServerStarted();
    }


    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        EnchantUtil.registerToolTips(e -> e.tooltip(event.getItemStack(), event.getFlags(), event.getToolTip()));
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = XEnchantment.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        private ClientModEvents() {
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EnchantUtil.onClient();

            ClientsideUtil.setMinecraft(Minecraft::getInstance);
            ClientsideUtil.setLocalPlayerGetter(() -> Minecraft.getInstance().player);
        }

        // Key mapping is lazily initialized so it doesn't exist until it is registered
        public static final Lazy<KeyMapping> EXAMPLE_MAPPING = Lazy.of(() -> new KeyMapping(
                "keybinding.category.x_enchantment.name",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "keybinding.key.x_enchantment.name"));

        // Event is on the mod event bus only on the physical client
        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            event.register(EXAMPLE_MAPPING.get());
        }
    }

    // Event is on the Forge event bus only on the physical client
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) { // Only call code once as the tick event is called twice every tick
            while (ClientModEvents.EXAMPLE_MAPPING.get().consumeClick()) {
                // Execute logic to perform on click here
                MenuScreen.open(Minecraft.getInstance());
            }
        }
    }
}