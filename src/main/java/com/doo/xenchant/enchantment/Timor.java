package com.doo.xenchant.enchantment;

import com.doo.xenchant.mixin.interfaces.ServerLivingApi;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
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
    public int getMinPower(int level) {
        return 1 + (level - 1) * 10;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 15;
    }

    @Override
    public void register() {
        super.register();

        ServerLivingApi.TAIL_TICK.register(living -> {
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
