package com.doo.xenchantment.enchantment.special;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

/**
 * Health Converter
 */
public class HealthConverter extends Special {

    public static final String HURT_KEY = "hurt";

    public HealthConverter() {
        super("health_converter", EnchantmentCategory.BREAKABLE, EquipmentSlot.values());

        options.addProperty(HURT_KEY, 10);
    }

    @Override
    protected boolean onlyOneLevel() {
        return true;
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, HURT_KEY);
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
        if (item.isEmpty() || item.getDamageValue() < 1) {
            return;
        }

        float damage = (float) getDouble(HURT_KEY);
        item.setDamageValue(0);
        living.hurt(player.damageSources().magic(), damage);
    }
}
