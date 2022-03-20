package com.doo.xenchant.enchantment;

import com.doo.xenchant.events.LivingApi;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Climber
 */
public class Climber extends BaseEnchantment {

    public static final String NAME = "climber";

    public Climber() {
        super(NAME, Rarity.COMMON, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public void register() {
        super.register();

        LivingApi.SEVER_TAIL_TICK.register(living -> {
            if (living.getY() < 80 || living.tickCount % SECOND != 0) {
                return;
            }

            ItemStack feet = living.getItemBySlot(EquipmentSlot.FEET);
            if (feet.isEmpty() || level(feet) < 1) {
                return;
            }

            living.addEffect(new MobEffectInstance(MobEffects.JUMP, (int) (SECOND * 2.5), 2));
        });
    }
}
