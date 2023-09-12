package com.doo.xenchantment.interfaces;

import com.doo.xenchantment.enchantment.BaseXEnchantment;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public interface Usable<T extends BaseXEnchantment> {

    boolean onUsed(Integer level, ItemStack stack, Player player, InteractionHand hand,
                   Consumer<InteractionResultHolder<ItemStack>> consumer);
}
