package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.LivingApi;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 魔法免疫
 */
public class MagicImmune extends BaseEnchantment {

    public static final String NAME = "magic_immune";

    public MagicImmune() {
        super(NAME, Rarity.VERY_RARE, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public void register() {
        super.register();

        LivingApi.SEVER_TAIL_TICK.register(living -> {
            if (!Enchant.option.magicImmune || living.tickCount % SECOND != 0) {
                return;
            }

            Collection<ItemStack> stacks = getSlotItems(living).values();
            if (stacks.size() < 1) {
                return;
            }

            stacks.stream().filter(s -> level(s) > 0).findFirst().ifPresent(s -> {
                // remove all badly effect
                Set<MobEffect> effects = living.getActiveEffects().stream()
                        .map(MobEffectInstance::getEffect)
                        .filter(effectType -> effectType.getCategory() == MobEffectCategory.HARMFUL)
                        .collect(Collectors.toSet());

                effects.forEach(living::removeEffect);
            });
        });

        LivingApi.IGNORED_APPLY_STATUS.register(((living, effect, source) ->
                Enchant.option.magicImmune && level(living.getItemBySlot(EquipmentSlot.CHEST)) > 0 && effect.getCategory() == MobEffectCategory.HARMFUL));
    }
}
