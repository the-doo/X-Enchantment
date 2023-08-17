package com.doo.xenchantment.events;

import com.google.common.collect.Lists;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface GrindstoneApi {

    final class InnerE {
        static final List<GrindstoneApi> EVENT = Lists.newArrayList();

        private InnerE() {
        }
    }

    static void register(GrindstoneApi callback) {
        GrindstoneApi.InnerE.EVENT.add(callback);
    }

    static void call(ItemStack result) {
        GrindstoneApi.InnerE.EVENT.forEach(l -> l.handle(result));
    }

    void handle(ItemStack result);
}
