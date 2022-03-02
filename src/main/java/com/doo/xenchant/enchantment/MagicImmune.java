package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.LivingApi;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 魔法免疫
 */
public class MagicImmune extends BaseEnchantment {

    public static final String NAME = "magic_immune";

    public MagicImmune() {
        super(NAME, Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public void register() {
        super.register();

        LivingApi.SEVER_TAIL_TICK.register(living -> {
            if (!Enchant.option.magicImmune || living.age % SECOND != 0) {
                return;
            }

            Collection<ItemStack> stacks = getEquipment(living).values();
            if (stacks.size() < 1) {
                return;
            }

            stacks.stream().filter(s -> level(s) > 0).findFirst().ifPresent(s -> {
                // remove all badly effect
                Set<StatusEffect> effects = living.getStatusEffects().stream()
                        .map(StatusEffectInstance::getEffectType)
                        .filter(effectType -> effectType.getCategory() == StatusEffectCategory.HARMFUL)
                        .collect(Collectors.toSet());

                effects.forEach(living::removeStatusEffect);
            });
        });

        LivingApi.IGNORED_APPLY_STATUS.register(((living, effect, source) ->
                Enchant.option.magicImmune && level(living.getEquippedStack(EquipmentSlot.CHEST)) > 0 && effect.getCategory() == StatusEffectCategory.HARMFUL));
    }
}
