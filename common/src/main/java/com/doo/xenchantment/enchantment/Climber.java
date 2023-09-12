package com.doo.xenchantment.enchantment;

import com.doo.xenchantment.interfaces.OneLevelMark;
import com.google.gson.JsonObject;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class Climber extends BaseXEnchantment implements OneLevelMark {

    private static final String Y_KEY = "y";

    public Climber() {
        super("climber", Rarity.UNCOMMON, EnchantmentCategory.ARMOR_LEGS, EquipmentSlot.LEGS);

        options.addProperty(Y_KEY, 80);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, Y_KEY);
    }

    @Override
    public void onEndTick(LivingEntity living) {
        if (living.tickCount % SECOND_TICK != 0 || living.getY() < doubleV(Y_KEY)) {
            return;
        }

        ItemStack feet = living.getItemBySlot(EquipmentSlot.LEGS);
        if (feet.isEmpty() || level(feet) < 1) {
            return;
        }

        living.addEffect(new MobEffectInstance(MobEffects.JUMP, (int) (SECOND_TICK * 2.5), 2));
    }
}
