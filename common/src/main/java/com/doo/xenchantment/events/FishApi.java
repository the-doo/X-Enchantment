package com.doo.xenchantment.events;

import com.google.common.collect.Lists;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public interface FishApi {

    final class InnerE {
        static final List<FishApi> EVENT = Lists.newArrayList();

        private InnerE() {
        }
    }


    static void register(FishApi callback) {
        InnerE.EVENT.add(callback);
    }

    static void call(ServerPlayer player) {
        InnerE.EVENT.forEach(l -> l.onCaught(player));
    }

    void onCaught(ServerPlayer player);
}
