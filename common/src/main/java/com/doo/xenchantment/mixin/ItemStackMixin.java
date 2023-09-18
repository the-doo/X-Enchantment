package com.doo.xenchantment.mixin;

import com.doo.playerinfo.XPlayerInfo;
import com.doo.xenchantment.events.ItemApi;
import com.doo.xenchantment.util.EnchantUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract boolean isEmpty();

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"))
    private void itemUsedCallback(int i, RandomSource randomSource, ServerPlayer serverPlayer, CallbackInfoReturnable<Boolean> cir) {
        ItemApi.call(serverPlayer, XPlayerInfo.get(this), i);
    }

    @Inject(method = "useOn", at = @At(value = "HEAD"), cancellable = true)
    private void enchantedBookUseOn(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir) {
        if (useOnContext.getLevel().isClientSide() || isEmpty() || !(getItem() instanceof EnchantedBookItem)) {
            return;
        }

        if (EnchantUtil.useOnBlock(useOnContext.getClickedPos(), XPlayerInfo.get(this), useOnContext.getPlayer(), useOnContext.getHand(), cir::setReturnValue)) {
            cir.cancel();
        }
    }

    @Inject(method = "use", at = @At(value = "HEAD"), cancellable = true)
    private void enchantedBookUse(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (level.isClientSide() || isEmpty() || !(getItem() instanceof EnchantedBookItem)) {
            return;
        }

        if (EnchantUtil.useBook(XPlayerInfo.get(this), player, interactionHand, cir::setReturnValue)) {
            cir.cancel();
        }
    }
}
