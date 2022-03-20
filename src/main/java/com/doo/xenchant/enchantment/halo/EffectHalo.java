package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.Enchant;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.List;

/**
 * Status Effect Halo - enchantment version of status effect
 */
public class EffectHalo extends LivingHalo {

    public static final String NAME = "effect";

    private final MobEffect effect;

    public EffectHalo(MobEffect effect) {
        super(NAME + "_-_" + effect.getDescriptionId());

        this.effect = effect;
    }

    @Override
    public String getDescriptionId() {
        return "enchantment.x_enchant.halo_effect";
    }

    @Override
    public Component getFullname(int level) {
        TranslatableComponent mutableText = new TranslatableComponent(getDescriptionId(), effect.getDisplayName());
        if (this.isTradeable()) {
            mutableText.withStyle(ChatFormatting.BLUE);
        } else {
            mutableText.withStyle(ChatFormatting.GRAY);
        }
        if (level != 1 || this.getMaxLevel() != 1) {
            mutableText.append(" ").append(new TranslatableComponent("enchantment.level." + level));
        }
        return mutableText;
    }

    @Override
    public int getMaxLevel() {
        return isTradeable() ? Enchant.option.effectTreasureMaxLevel : Enchant.option.effectOtherMaxLevel;
    }

    @Override
    public boolean isTradeable() {
        return effect == null || effect.isInstantenous() || effect.isBeneficial();
    }

    @Override
    protected boolean ban(LivingEntity living) {
        return Enchant.option.disabledEffect.contains(effect.getDescriptionId());
    }

    @Override
    public Type getType() {
        return effect.getCategory() == MobEffectCategory.HARMFUL ? Type.HARMFUL : Type.FRIENDLY;
    }

    @Override
    public void onTarget(LivingEntity entity, Integer level, List<LivingEntity> targets) {
        // default duration 1.5 * second()
        int duration = (int) (SECOND * 1.5F);
        // NIGHT_VISION has special effects in duration < 200
        // see net.minecraft.client.render.GameRenderer.getNightVisionStrength
        if (effect == MobEffects.NIGHT_VISION) {
            duration *= 10;
        }

        MobEffectInstance instance = new MobEffectInstance(effect, duration, level - 1);
        targets.forEach(e -> {
            if (!effect.getAttributeModifiers().containsKey(Attributes.MAX_HEALTH)) {
                e.addEffect(instance);
                return;
            }

            // if it is HEALTH_BOOST
            MobEffectInstance has = e.getEffect(effect);

            if (has == null) {
                e.addEffect(instance);
            } else {
                has.update(instance);
            }
        });
    }
}
