package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.LivingApi;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

/**
 * 快速射击
 */
public class QuickShot extends BaseEnchantment {

    public static final String NAME = "quick_shot";

    public QuickShot() {
        super(NAME, Rarity.UNCOMMON, EnchantmentTarget.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void register() {
        super.register();

        LivingApi.REDUCE_USE_TIME.register(((living, stack) -> !Enchant.option.quickShot ? 0 : Math.max(level(stack), 0)));
    }
}
