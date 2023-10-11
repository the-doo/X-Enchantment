package com.doo.xenchantment.fabric.mixin;

import com.doo.xenchantment.util.EnchantUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheep.class)
public abstract class SheepMixin {

    @Unique
    private Player player;
    @Unique
    private ItemStack stack;

    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Sheep;shear(Lnet/minecraft/sounds/SoundSource;)V"))
    private void logPlayerAndStack(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        this.player = player;
        this.stack = player.getItemInHand(interactionHand);
    }

    @ModifyVariable(method = "shear", at = @At("STORE"), ordinal = 0)
    private int interactAt(int i) {
        i = EnchantUtil.lootShearsFabric(player, stack, i);
        player = null;
        stack = null;
        return i;
    }
}
