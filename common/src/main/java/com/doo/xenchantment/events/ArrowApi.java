package com.doo.xenchantment.events;

import com.google.common.collect.Lists;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ArrowApi {

    final class InnerE {
        static final List<ArrowApi> EVENT = Lists.newArrayList();

        private InnerE() {
        }
    }


    static void register(ArrowApi callback) {
        InnerE.EVENT.add(callback);
    }

    static void call(AbstractArrow arrow, LivingEntity attacker, ItemStack itemStack, LivingEntity entity, float damage) {
        InnerE.EVENT.forEach(l -> l.onHit(arrow, attacker, itemStack, entity, damage));
    }

    void onHit(AbstractArrow arrow, LivingEntity attacker, ItemStack itemStack, LivingEntity entity, float damage);
}
