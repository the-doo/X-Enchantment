package com.doo.xenchant.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Persistent Projectile Entity Api - like EntityDamageApi
 */
public interface PersistentApi {

    Event<OnColl> ON_COLL = EventFactory.createArrayBacked(OnColl.class, callback -> (((player, stack, world, pos, box) -> {
        Entity entity;
        for (OnColl onColl : callback) {
            entity = onColl.getEntity(player, stack, world, pos, box);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    })));

    Event<OpSpeed> ADD = EventFactory.createArrayBacked(OpSpeed.class,
            callback -> (living, stack) -> (float) Arrays.stream(callback).mapToDouble(c -> c.get(living, stack)).sum());

    Event<OpSpeed> MULTIPLIER = EventFactory.createArrayBacked(OpSpeed.class,
            callback -> (living, stack) -> (float) Arrays.stream(callback).mapToDouble(c -> c.get(living, stack)).sum());

    @FunctionalInterface
    interface OnColl {
        Entity getEntity(Entity player, ItemStack stack, World world, Vec3d pos, Box box);
    }

    @FunctionalInterface
    interface OpSpeed {
        float get(Entity owner, @Nullable ItemStack stack);
    }

    static float projSpeed(float speed, Entity owner, ItemStack shooter) {
        if (owner instanceof LivingEntity && shooter != null) {
            speed += PersistentApi.ADD.invoker().get(owner, shooter);
            if (speed <= 0) {
                return 0;
            }

            speed *= (1 + PersistentApi.MULTIPLIER.invoker().get(owner, shooter) / 100F);
            if (speed <= 0) {
                return 0;
            }
        }
        return speed;
    }
}
