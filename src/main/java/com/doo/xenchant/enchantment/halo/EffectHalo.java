package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.Enchant;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
        return isTreasure() ? Enchant.option.effectTreasureMaxLevel : Enchant.option.effectOtherMaxLevel;
    }

    @Override
    public boolean isTreasure() {
        return effect == null || effect.isInstant() || effect.isBeneficial();
    }

    @Override
    protected boolean ban(LivingEntity living) {
        return Enchant.option.disabledEffect.contains(effect.getTranslationKey());
    }

    @Override
    public Type getType() {
        return effect.getCategory() == StatusEffectCategory.HARMFUL ? Type.HARMFUL : Type.FRIENDLY;
    }

    @Override
    public void onTarget(LivingEntity entity, Integer level, List<LivingEntity> targets) {
        // default duration 1.5 * second()
        int duration = (int) (SECOND * 1.5F);
        // NIGHT_VISION has special effects in duration < 200
        // see net.minecraft.client.render.GameRenderer.getNightVisionStrength
        if (effect == StatusEffects.NIGHT_VISION) {
            duration *= 10;
        }

        StatusEffectInstance instance = new StatusEffectInstance(effect, duration, level - 1);
        targets.forEach(e -> {
            if (effect != StatusEffects.HEALTH_BOOST) {
                e.addStatusEffect(instance);
                return;
            }

            // if it is HEALTH_BOOST
            StatusEffectInstance has = e.getStatusEffect(effect);

            if (has == null) {
                e.addStatusEffect(instance);
            } else {
                has.upgrade(instance);
            }
        });
    }
}
