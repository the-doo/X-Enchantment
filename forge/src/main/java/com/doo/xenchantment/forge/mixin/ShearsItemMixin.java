package com.doo.xenchantment.forge.mixin;

import com.doo.xenchantment.util.EnchantUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(ShearsItem.class)
public abstract class ShearsItemMixin {

    @ModifyVariable(method = "interactLivingEntity", at = @At("STORE"), ordinal = 0)
    private List<ItemStack> onSheared(List<ItemStack> drop, ItemStack stack, Player playerIn, LivingEntity entity, InteractionHand hand) {
        EnchantUtil.lootShearsForge(drop, playerIn, entity, stack);
        return drop;
    }
}
