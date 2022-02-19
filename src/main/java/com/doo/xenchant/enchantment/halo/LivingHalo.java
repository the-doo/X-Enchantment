package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.Enchant;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
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

    public Type getType() {
        return Type.FRIENDLY;
    }

    @Override
    protected final List<LivingEntity> targets(LivingEntity living, Box box) {
        if (living == null) {
            return Collections.emptyList();
        }

        Predicate<LivingEntity> filter = e -> getType().predicate.test(living, e);
        // if harmful is monster used, target only player
        if (getType() == Type.HARMFUL && living instanceof Monster) {
            filter = Entity::isPlayer;
        }

        return living.world.getEntitiesByClass(LivingEntity.class, box, filter);
    }

    /**
     * Target is who in halo
     */
    public enum Type {
        FRIENDLY((self, target) ->
                !target.isSpectator() && (target == self || target.isTeammate(self))),

        HARMFUL((self, target) ->
                !target.isSpectator() && target != self && !target.isTeammate(self) &&
                        // if open
                        (!Enchant.option.harmfulTargetOnlyMonster || target instanceof Monster)),

        ;

        public final BiPredicate<LivingEntity, LivingEntity> predicate;

        Type(BiPredicate<LivingEntity, LivingEntity> predicate) {
            this.predicate = predicate;
        }
    }
}
