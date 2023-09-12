package com.doo.xenchantment.interfaces;

import com.doo.xenchantment.enchantment.BaseXEnchantment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public interface Tooltipsable<T extends BaseXEnchantment> {

    void tooltip(ItemStack stack, TooltipFlag context, List<Component> lines);
}
