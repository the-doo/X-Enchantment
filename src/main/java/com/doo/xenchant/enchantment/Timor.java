package com.doo.xenchant.enchantment;

import com.doo.xenchant.events.LivingApi;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Timor
 */
public class Timor extends BaseEnchantment {

    public static final String NAME = "timor";

    private static final String ID = "Id";

    private static final String COUNT = "Count";

    private static final String POS = "Pos";

    public Timor() {
        super(NAME, Rarity.COMMON, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public void register() {
        super.register();

        LivingApi.SEVER_TAIL_TICK.register(living -> {
            if (living.tickCount % SECOND != 0) {
                return;
            }

            ItemStack stack = living.getItemBySlot(EquipmentSlot.FEET);
            if (stack.isEmpty() || level(stack) < 1) {
                return;
            }

            CompoundTag nbt = stack.getOrCreateTag();
            long id = nbt.getLong(nbtKey(ID));
            if (living.getId() != id) {
                nbt.putLong(nbtKey(ID), living.getId());
                nbt.putLong(nbtKey(COUNT), 1);
                nbt.remove(nbtKey(POS));
            }
            long count = nbt.getLong(nbtKey(COUNT));

            // if it's not standing
            if (living.getPose() != Pose.CROUCHING) {
                if (count >= 3) {
                    living.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 300, 3));
                }

                nbt.remove(nbtKey(COUNT));
                nbt.remove(nbtKey(ID));
                return;
            }

            // hide on 1.5s
            if (count >= 3) {
                living.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 30));
            }

            // log count
            nbt.putLong(nbtKey(COUNT), count + 1);
        });
    }
}
