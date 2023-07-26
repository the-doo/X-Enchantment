package com.doo.xenchantment.events;

import com.google.common.collect.Lists;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public interface LootApi {

    final class InnerE {
        static final List<LootApi> EVENT = Lists.newArrayList();

        private InnerE() {
        }
    }

    static void register(LootApi register) {
        LootApi.InnerE.EVENT.add(register);
    }

    static List<ItemStack> trigger(LivingEntity entity, ItemStack trigger, List<ItemStack> stack) {
        if (stack.isEmpty() || stack.size() < 2 && stack.get(0).isEmpty()) {
            return Collections.emptyList();
        }

        List<ItemStack> stacks = Lists.newArrayList();
        LootApi.InnerE.EVENT.forEach(l -> stacks.addAll(l.addition(entity, trigger, stack)));
        return stacks;
    }

    /**
     * return addition loot
     */
    List<ItemStack> addition(LivingEntity entity, ItemStack stack, List<ItemStack> immStacks);
}
