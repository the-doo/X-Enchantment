package com.doo.xenchant.enchantment.halo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Friendly halo
 */
public abstract class FriendHalo extends HaloEnchantment<LivingEntity> {

    public FriendHalo(String name) {
        super(name);
    }

    @Override
    protected List<LivingEntity> targets(LivingEntity living, Box box) {
        if (living == null) {
            return Collections.emptyList();
        }

        Predicate<LivingEntity> filter = e -> !e.isSpectator() && (e == living || living.isTeammate(e));
        return living.world.getEntitiesByClass(LivingEntity.class, box, filter);
    }
}
