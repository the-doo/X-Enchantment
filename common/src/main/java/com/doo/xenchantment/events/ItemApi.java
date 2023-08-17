package com.doo.xenchantment.events;

import com.google.common.collect.Lists;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ItemApi {

    final class InnerE {
        static final List<ItemApi> EVENT = Lists.newArrayList();

        private InnerE() {
        }
    }


    static void register(ItemApi callback) {
        ItemApi.InnerE.EVENT.add(callback);
    }

    static void call(LivingEntity owner, ItemStack stack, float amount) {
        ItemApi.InnerE.EVENT.forEach(l -> l.onDamaged(owner, stack, amount));
    }

    void onDamaged(LivingEntity owner, ItemStack stack, float amount);
}
