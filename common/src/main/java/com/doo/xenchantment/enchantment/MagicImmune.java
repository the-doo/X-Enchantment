package com.doo.xenchantment.enchantment;

import com.google.common.collect.Lists;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.List;

public class MagicImmune extends BaseXEnchantment {

    private static final List<MobEffect> EFFECTS = Lists.newArrayList();

    public MagicImmune() {
        super("magic_immune", Rarity.VERY_RARE, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    protected boolean onlyOneLevel() {
        return true;
    }

    @Override
    public void onServer() {
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
        return !(level(living.getItemBySlot(EquipmentSlot.CHEST)) > 1 && EFFECTS.contains(effect.getEffect()));
    }
}
