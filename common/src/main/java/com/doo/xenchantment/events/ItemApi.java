package com.doo.xenchantment.events;

import com.google.common.collect.Lists;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ItemApi {

    final class InnerE {
        static final List<OnDamaged> ON_DAMAGED = Lists.newArrayList();
        static final List<BeforeDamaged> BEFORE_DAMAGED = Lists.newArrayList();

        private InnerE() {
        }
    }


    static void registerOnDamaged(OnDamaged callback) {
        ItemApi.InnerE.ON_DAMAGED.add(callback);
    }

    static void callOnDamaged(LivingEntity owner, ItemStack stack, float amount) {
        ItemApi.InnerE.ON_DAMAGED.forEach(l -> l.onDamaged(owner, stack, amount));
    }


    static void registerBeforeDamaged(BeforeDamaged callback) {
        ItemApi.InnerE.BEFORE_DAMAGED.add(callback);
    }

    static void callBeforeDamaged(LivingEntity owner, ItemStack stack, float amount) {
        ItemApi.InnerE.BEFORE_DAMAGED.forEach(l -> l.beforeDamaged(owner, stack, amount));
    }

    interface OnDamaged {
        void onDamaged(LivingEntity owner, ItemStack stack, float amount);
    }

    interface BeforeDamaged {
        void beforeDamaged(LivingEntity owner, ItemStack stack, float amount);
    }
}
