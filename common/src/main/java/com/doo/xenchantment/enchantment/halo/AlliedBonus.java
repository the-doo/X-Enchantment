package com.doo.xenchantment.enchantment.halo;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AlliedBonus extends Halo {

    private static final List<MobEffect> EFFECTS = Lists.newArrayList();

    private static final List<MobEffect> BAN = Lists.newArrayList();

    private static final String EFFECT_TIME_KEY = "effect_time";
    private static final String EFFECT_LEVEL_KEY = "effect_level";
    private static final String BAN_KEY = "ban";

    protected AlliedBonus(EquipmentSlot slot) {
        super("allied_bonus", Rarity.RARE, slot);
    }

    @Override
    public void initHaloFirstOptions() {
        options.addProperty(INTERVAL_KEY, 3);
        options.addProperty(EFFECT_TIME_KEY, 1);
        options.addProperty(EFFECT_LEVEL_KEY, 1);
        options.add(BAN_KEY, new JsonArray());
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, INTERVAL_KEY);
        loadIf(json, EFFECT_TIME_KEY);
        loadIf(json, EFFECT_LEVEL_KEY);
        loadIf(json, BAN_KEY);

        BAN.clear();
        foreach(BAN_KEY, id -> EFFECTS.stream()
                .filter(effect -> effect.getDescriptionId().equals(id.getAsString()))
                .forEach(BAN::add));

    }

    @Override
    public void onOptionsRegister(BiConsumer<String, Supplier<Stream<String>>> register) {
        super.onOptionsRegister(register);

        register.accept(BAN_KEY, () -> EFFECTS.stream().map(MobEffect::getDescriptionId));
    }

    @Override
    public void onClient() {
        EFFECTS.add(MobEffects.HEALTH_BOOST);
        EFFECTS.add(MobEffects.DAMAGE_BOOST);
        EFFECTS.add(MobEffects.DAMAGE_RESISTANCE);
        EFFECTS.add(MobEffects.MOVEMENT_SPEED);
    }

    @Override
    public void onServer(MinecraftServer server) {
        if (EFFECTS.isEmpty()) {
            EFFECTS.add(MobEffects.HEALTH_BOOST);
            EFFECTS.add(MobEffects.DAMAGE_BOOST);
            EFFECTS.add(MobEffects.DAMAGE_RESISTANCE);
            EFFECTS.add(MobEffects.MOVEMENT_SPEED);
        }
    }

    @Override
    protected void trigger(LivingEntity living, AABB box) {
        int tick = (int) (interval() * getDouble(EFFECT_TIME_KEY));
        int level = (int) getDouble(EFFECT_LEVEL_KEY);
        if (BAN.size() >= EFFECTS.size() || tick < 0 || level < 0) {
            return;
        }

        Predicate<LivingEntity> pass = e -> e.isAlliedTo(living) && !(e instanceof Player);
        List<MobEffectInstance> list = EFFECTS.stream()
                .filter(e -> !BAN.contains(e))
                .map(e -> new MobEffectInstance(e, tick, level))
                .toList();
        living.level().getEntitiesOfClass(LivingEntity.class, box, pass).forEach(e -> list.forEach(e::addEffect));
    }
}
