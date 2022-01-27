package com.doo.xenchant.enchantment.halo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * Status Effect Halo - enchantment version of status effect
 */
public class EffectHalo extends LivingHalo {

    public static final String NAME = "effect";

    private final StatusEffect effect;

    public EffectHalo(StatusEffect effect) {
        super(NAME + "_-_" + effect.getTranslationKey());

        this.effect = effect;
    }

    @Override
    public String getTranslationKey() {
        return "enchantment.x_enchant.halo_effect";
    }

    @Override
    public Text getName(int level) {
        TranslatableText mutableText = new TranslatableText(getTranslationKey(), effect.getName().getString());
        if (this.isTreasure()) {
            mutableText.formatted(Formatting.BLUE);
        } else {
            mutableText.formatted(Formatting.GRAY);
        }
        if (level != 1 || this.getMaxLevel() != 1) {
            mutableText.append(" ").append(new TranslatableText("enchantment.level." + level));
        }
        return mutableText;
    }

    @Override
    public int getMaxLevel() {
        return isTreasure() ? 5 : 3;
    }

    @Override
    public boolean isTreasure() {
        return effect == null || effect.isInstant() || effect.isBeneficial();
    }

    @Override
    public Type getType() {
        return effect.getCategory() == StatusEffectCategory.HARMFUL ? Type.HARMFUL : Type.FRIENDLY;
    }

    @Override
    protected int second() {
        return 2;
    }

    @Override
    public void onTarget(LivingEntity entity, Integer level, List<LivingEntity> targets) {
        // default duration 2 * second()
        StatusEffectInstance instance = new StatusEffectInstance(effect, second() * 25, level);
        targets.forEach(e -> {
            StatusEffectInstance has = e.getStatusEffect(effect);

            if (has == null) {
                e.addStatusEffect(instance);
            } else {
                has.upgrade(instance);
            }
        });
    }
}
