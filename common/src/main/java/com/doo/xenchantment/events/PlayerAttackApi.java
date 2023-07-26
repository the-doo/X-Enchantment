package com.doo.xenchantment.events;

import com.google.common.collect.Lists;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public interface PlayerAttackApi {

    final class InnerE {
        static final List<PlayerAttackApi> EVENT = Lists.newArrayList();

        private InnerE() {
        }
    }


    static void register(PlayerAttackApi callback) {
        InnerE.EVENT.add(callback);
    }

    static void call(ServerPlayer player, float amount) {
        InnerE.EVENT.forEach(l -> l.endAttack(player, amount));
    }

    void endAttack(ServerPlayer player, float amount);
}
