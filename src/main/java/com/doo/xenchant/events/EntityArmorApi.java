package com.doo.xenchant.events;

import com.doo.xenchant.enchantment.BaseEnchantment;
import com.doo.xenchant.util.EnchantUtil;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

import java.util.Arrays;
import java.util.Map;

/**
 * Entity get Armor - like EntityDamageApi
 */
public interface EntityArmorApi {

    Event<OpArmor> ADD = EventFactory.createArrayBacked(OpArmor.class,
            callback -> (living, base, map) -> (float) Arrays.stream(callback).mapToDouble(c -> c.get(living, base, map)).sum());

    Event<OpArmor> MULTIPLIER = EventFactory.createArrayBacked(OpArmor.class,
            callback -> (living, base, map) -> (float) Arrays.stream(callback).mapToDouble(c -> c.get(living, base, map)).sum());

    @FunctionalInterface
    interface OpArmor {
        float get(LivingEntity living, double base, Map<BaseEnchantment, Pair<Integer, Integer>> map);
    }

    static double armor(double base, LivingEntity living) {
        Map<BaseEnchantment, Pair<Integer, Integer>> map = EnchantUtil.mergeOf(living);
        base += EntityArmorApi.ADD.invoker().get(living, base, map);
        if (base <= 0) {
            return 0;
        }

        base *= (1 + EntityArmorApi.MULTIPLIER.invoker().get(living, base, map) / 100F);
        if (base <= 0) {
            return 0;
        }
        return base;
    }
}
