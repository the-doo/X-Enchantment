package com.doo.xenchant.enchantment.trinkets;

import com.doo.xenchant.events.EntityDamageApi;

import java.util.Optional;

/**
 * With Speed
 */
public class WithArmor extends Trinkets {

    public static final String NAME = "with_speed";

    public WithArmor() {
        super(NAME);
    }

    @Override
    public void register() {
        super.register();

        EntityDamageApi.MULTIPLIER.register(((attacker, target, map) -> {
            if (!map.containsKey(this)) {
                return 0;
            }

            return Optional.ofNullable(map.get(this)).orElse(0) * .2F;
        }));
    }
}
