package com.doo.xenchantment.enchantment;

import com.doo.xenchantment.interfaces.OneLevelMark;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MagicImmune extends BaseXEnchantment implements OneLevelMark {

    private static final List<MobEffect> EFFECTS = Lists.newArrayList();

    private static final List<MobEffect> BAN = Lists.newArrayList();

    private static final String BAN_KEY = "ban";

    public MagicImmune() {
        super("magic_immune", Rarity.VERY_RARE, EnchantmentCategory.ARMOR_CHEST, EquipmentSlot.CHEST);

        options.add(BAN_KEY, new JsonArray());
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, BAN_KEY);

        BAN.clear();

        foreach(BAN_KEY, e -> EFFECTS.stream()
                .filter(effect -> effect.getDescriptionId().equals(e.getAsString()))
                .findAny().ifPresent(BAN::add));
    }

    @Override
    public void onOptionsRegister(BiConsumer<String, Supplier<Stream<String>>> register) {
        super.onOptionsRegister(register);

        register.accept(BAN_KEY, () -> BuiltInRegistries.MOB_EFFECT.stream()
                .filter(e -> e.getCategory() == MobEffectCategory.HARMFUL)
                .map(MobEffect::getDescriptionId)
                .distinct());
    }

    @Override
    public void onServer(MinecraftServer server) {
        BuiltInRegistries.MOB_EFFECT.stream()
                .filter(e -> e.getCategory() == MobEffectCategory.HARMFUL)
                .forEach(EFFECTS::add);
    }

    @Override
    public void onEndTick(LivingEntity living) {
        if (living.tickCount % SECOND_TICK != 0) {
            return;
        }

        int level = level(living.getItemBySlot(EquipmentSlot.CHEST));
        if (level < 1) {
            return;
        }

        EFFECTS.forEach(living::removeEffect);
    }

    @Override
    public boolean allowEffectAddition(MobEffectInstance effect, LivingEntity living) {
        return !(!BAN.contains(effect.getEffect()) && level(living.getItemBySlot(EquipmentSlot.CHEST)) > 1 && EFFECTS.contains(effect.getEffect()));
    }
}
