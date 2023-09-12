package com.doo.xenchantment.interfaces;

import com.doo.xenchantment.advancements.TrueTrigger;
import com.doo.xenchantment.enchantment.BaseXEnchantment;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public interface Advable<T extends BaseXEnchantment> {

    TrueTrigger getAdvTrigger();
}
