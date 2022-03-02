package com.doo.xenchant.enchantment;

import com.doo.xenchant.events.LivingApi;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

/**
 * Timor
 */
public class Timor extends BaseEnchantment {

    public static final String NAME = "timor";

    private static final String ID = "Id";

    private static final String COUNT = "Count";

    private static final String POS = "Pos";

    public Timor() {
        super(NAME, Rarity.COMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public void register() {
        super.register();

        LivingApi.SEVER_TAIL_TICK.register(living -> {
            if (living.age % SECOND != 0) {
                return;
            }

            ItemStack stack = living.getEquippedStack(EquipmentSlot.FEET);
            if (stack.isEmpty() || level(stack) < 1) {
                return;
            }

            NbtCompound nbt = stack.getOrCreateNbt();
            long id = nbt.getLong(nbtKey(ID));
            if (living.getId() != id) {
                nbt.putLong(nbtKey(ID), living.getId());
                nbt.putLong(nbtKey(COUNT), 1);
                nbt.remove(nbtKey(POS));
            }
            long count = nbt.getLong(nbtKey(COUNT));

            // if it's not standing
            if (living.getPose() != EntityPose.CROUCHING) {
                if (count >= 3) {
                    living.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 300, 3));
                }

                nbt.remove(nbtKey(COUNT));
                nbt.remove(nbtKey(ID));
                return;
            }

            // hide on 1.5s
            if (count >= 3) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 30));
            }

            // log count
            nbt.putLong(nbtKey(COUNT), count + 1);
        });
    }
}
