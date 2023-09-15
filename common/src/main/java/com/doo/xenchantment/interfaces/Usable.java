package com.doo.xenchantment.interfaces;

import com.doo.xenchantment.enchantment.BaseXEnchantment;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public interface Usable<T extends BaseXEnchantment> {

    default boolean onUsed(Integer level, ItemStack stack, Player player, InteractionHand hand,
                           Consumer<InteractionResultHolder<ItemStack>> consumer) {
        return false;
    }

    default boolean useBookOn(Integer value, ItemStack stack, Player player, InteractionHand hand,
                              BlockState state, Block block, BlockEntity entity, Consumer<InteractionResult> consumer) {
        return false;
    }
}
