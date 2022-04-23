package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

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
    protected final List<LivingEntity> targets(LivingEntity living, AABB box) {
        if (living == null) {
            return Collections.emptyList();
        }

        Predicate<LivingEntity> filter = e -> getType().predicate.test(living, e);
        // if harmful is monster used, target only player
        if (getType() == Type.HARMFUL && living instanceof Monster) {
            filter = e -> e instanceof Player;
        }

        return living.level.getEntitiesOfClass(LivingEntity.class, box, filter);
    }

    /**
     * Target is who in halo
     */
    public enum Type {
        FRIENDLY((self, target) ->
                !target.isSpectator() && (target == self || hasSameTeam(self, target))),

        HARMFUL((self, target) ->
                !target.isSpectator() && target != self && !hasSameTeam(self, target) &&
                        // if open
                        (!Enchant.option.harmfulTargetOnlyMonster || target instanceof Monster)),

        ;

        public final BiPredicate<LivingEntity, LivingEntity> predicate;

        Type(BiPredicate<LivingEntity, LivingEntity> predicate) {
            this.predicate = predicate;
        }
    }

    private static boolean hasSameTeam(LivingEntity owner, LivingEntity target) {
        if (target.isAlliedTo(owner)) {
            return true;
        }

        // support ftb team
        if (!EnchantUtil.hasFTBTeam || !(owner instanceof ServerPlayer)) {
            return false;
        }
        // same team
        if (target instanceof ServerPlayer) {
            return FTBTeamsAPI.arePlayersInSameTeam((ServerPlayer) owner, (ServerPlayer) target);
        }
        // pet same team
        if (target instanceof TamableAnimal && ((TamableAnimal) target).getOwner() instanceof ServerPlayer) {
            return FTBTeamsAPI.arePlayersInSameTeam((ServerPlayer) owner, (ServerPlayer) ((TamableAnimal) target).getOwner());
        }
        return false;
    }
}
