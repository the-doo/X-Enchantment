package com.doo.xenchantment.events;

import com.google.common.collect.Lists;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

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

    static List<ItemStack> trigger(LivingEntity entity, RandomSource random, ItemStack trigger, List<ItemStack> stack, boolean effectBlock) {
        if (stack.isEmpty() || stack.size() < 2 && stack.get(0).isEmpty()) {
            return Collections.emptyList();
        }

        List<ItemStack> stacks = Lists.newArrayList();
        LootApi.InnerE.EVENT.forEach(l -> stacks.addAll(l.addition(entity, random, trigger, stack, effectBlock)));
        return stacks;
    }

    /**
     * return addition loot
     */
    List<ItemStack> addition(@Nullable LivingEntity entity, RandomSource random, ItemStack stack, List<ItemStack> immStacks, boolean effectBlock);
}
