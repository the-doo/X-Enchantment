package com.doo.xenchantment.enchantment.special;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Health Converter
 */
public class HealthConverter extends Special {

    public static final String HURT_KEY = "hurt";

    public static final String BAN_KEY = "ban";

    private static final List<Item> BAN_LIST = Lists.newArrayList();

    public HealthConverter() {
        super("health_converter", EnchantmentCategory.BREAKABLE, EquipmentSlot.values());

        options.addProperty(HURT_KEY, 10);
        options.add(BAN_KEY, new JsonArray());
    }

    @Override
    protected boolean onlyOneLevel() {
        return true;
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, HURT_KEY);
        loadIf(json, BAN_KEY);

        BAN_LIST.clear();
        foreach(BAN_KEY, e -> BuiltInRegistries.ITEM.getOptional(new ResourceLocation(e.getAsString()))
                .ifPresent(BAN_LIST::add));
    }

    @Override
    public void onOptionsRegister(BiConsumer<String, Supplier<Stream<String>>> register) {
        register.accept(BAN_KEY, () -> BuiltInRegistries.ITEM.stream()
                .filter(i -> i.getDefaultInstance().isDamageableItem())
                .map(Item::getDescriptionId));
    }

    @Override
    public boolean canEnchant(@NotNull ItemStack itemStack) {
        return false;
    }

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment enchantment) {
        return false;
    }

    @Override
    public void onEndTick(LivingEntity living) {
        if (!(living instanceof ServerPlayer player) || living.tickCount % (10 * SECOND_TICK) != 0) {
            return;
        }

        if (level(living.getOffhandItem()) < 1) {
            return;
        }

        ItemStack item = living.getMainHandItem();
        if (item.isEmpty() || BAN_LIST.contains(item.getItem()) || !item.isDamageableItem() || item.getDamageValue() < 1) {
            return;
        }

        float damage = (float) getDouble(HURT_KEY);
        item.setDamageValue(0);
        living.hurt(player.damageSources().magic(), damage);

        player.playSound(SoundEvents.ANVIL_USE);
    }
}
