package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.core.InfoGroupItems;
import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Predicate;

public class HeightAttacked extends BaseXEnchantment {

    private static final String MONSTER_KEY = "monster";
    private static final String TIME_KEY = "time";
    private static final String RANGE_KEY = "range";
    private static final String DAMAGE_KEY = "damage";

    public HeightAttacked() {
        super("height_advantage", Rarity.RARE, EnchantmentCategory.ARMOR_LEGS, EquipmentSlot.LEGS);

        options.addProperty(MAX_LEVEL_KEY, 4);
        options.addProperty(MONSTER_KEY, true);
        options.addProperty(TIME_KEY, 5);
        options.addProperty(RANGE_KEY, 5);
        options.addProperty(DAMAGE_KEY, 2);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, MONSTER_KEY);
        loadIf(json, TIME_KEY);
        loadIf(json, RANGE_KEY);
        loadIf(json, DAMAGE_KEY);
    }

    @Override
    public void onEndTick(LivingEntity living) {
        if (living.tickCount % (SECOND_TICK * (int) doubleV(TIME_KEY)) != 0) {
            return;
        }

        int level = level(living.getItemBySlot(EquipmentSlot.LEGS));
        if (level < 1) {
            return;
        }

        AABB aabb = living.getBoundingBox();
        Predicate<Entity> other = e -> !e.isAlliedTo(living) && (!boolV(MONSTER_KEY) || e instanceof Monster);
        List<Entity> entities = living.level().getEntities(living, aabb.inflate(doubleV(RANGE_KEY)), other);
        DamageSource attack = living.damageSources().mobAttack(living);
        float value = (float) (level * doubleV(DAMAGE_KEY));
        entities.stream().filter(e -> living.getEyeHeight() > e.getEyeHeight()).forEach(e -> e.hurt(attack, value));
    }

    @Override
    public InfoGroupItems collectPlayerInfo(ServerPlayer player) {
        ItemStack stack = player.getItemBySlot(EquipmentSlot.LEGS);
        int level = level(stack);
        InfoGroupItems group = InfoGroupItems.groupKey(getDescriptionId());
        group.add(getInfoKey(MONSTER_KEY), boolV(MONSTER_KEY), false);
        group.add(getInfoKey(TIME_KEY), doubleV(TIME_KEY), false);
        group.add(getInfoKey(RANGE_KEY), doubleV(RANGE_KEY), false);
        group.add(getInfoKey(DAMAGE_KEY), level < 1 ? 0 : level * doubleV(DAMAGE_KEY), false);
        return group;
    }
}
