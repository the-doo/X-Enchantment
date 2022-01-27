package com.doo.xenchant.enchantment.halo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;

import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Living halo
 */
public abstract class LivingHalo extends HaloEnchantment<LivingEntity> {

    public LivingHalo(String name) {
        super(name);
    }

    @Override
    protected boolean needTick() {
        return true;
    }

    public Type getType() {
        return Type.FRIENDLY;
    }

    @Override
    protected final List<LivingEntity> targets(LivingEntity living, Box box) {
        if (living == null) {
            return Collections.emptyList();
        }

        Predicate<LivingEntity> filter = e -> getType().predicate.test(living, e);
        return living.world.getEntitiesByClass(LivingEntity.class, box, filter);
    }

    /**
     * Target is who in halo
     */
    protected enum Type {
        FRIENDLY((self, target) -> !target.isSpectator() && (target == self || target.isTeammate(self))),

        HARMFUL((self, target) -> !target.isSpectator() && target != self && !target.isTeammate(self)),

        ;

        private final BiPredicate<LivingEntity, LivingEntity> predicate;

        Type(BiPredicate<LivingEntity, LivingEntity> predicate) {
            this.predicate = predicate;
        }
    }
}
