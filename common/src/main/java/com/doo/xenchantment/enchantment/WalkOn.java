package com.doo.xenchantment.enchantment;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Timor
 */
public class WalkOn extends BaseXEnchantment {

    private static final String BAN_KEY = "ban";

    private static final List<Fluid> BAN = Lists.newArrayList();

    public WalkOn() {
        super("walk_on", Rarity.RARE, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});

        options.add(BAN_KEY, new JsonArray());
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, BAN_KEY);

        BAN.clear();
        foreach(BAN_KEY, e -> BuiltInRegistries.FLUID.getOptional(new ResourceLocation(e.getAsString())).ifPresent(BAN::add));
    }

    @Override
    public void onOptionsRegister(BiConsumer<String, Supplier<Stream<String>>> register) {
        register.accept(BAN_KEY,
                () -> BuiltInRegistries.FLUID.stream()
                        .filter(f -> f.defaultFluidState().isSource())
                        .map(f -> BuiltInRegistries.FLUID.getKey(f).toLanguageKey()));
    }

    @Override
    protected boolean onlyOneLevel() {
        return true;
    }

    @Override
    public boolean canStandOnFluid(LivingEntity living, FluidState fluidState) {
        if (disabled() || level(living.getItemBySlot(EquipmentSlot.FEET)) < 1) {
            return false;
        }

        return !BAN.contains(fluidState.getType()) && fluidState.getTags().findFirst().filter(tag -> living.getFluidHeight(tag) <= 0.5).isPresent();
    }

    @Override
    public boolean canEntityWalkOnPowderSnow(LivingEntity e) {
        return !disabled() && level(e.getItemBySlot(EquipmentSlot.FEET)) > 0;
    }
}
