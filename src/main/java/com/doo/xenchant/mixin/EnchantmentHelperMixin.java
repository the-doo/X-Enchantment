package com.doo.xenchant.mixin;

import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(at = @At(value = "TAIL"), method = "getAttackDamage", cancellable = true)
    private static void addBaseEnchantmentAttackDamage(ItemStack stack, EntityGroup group, CallbackInfoReturnable<Float> cir) {
        MutableFloat damage = new MutableFloat(cir.getReturnValue());
        EnchantUtil.forBaseEnchantment((enchantment, level) -> damage.add(enchantment.getAttackDamage(stack, level, group)), stack);
        cir.setReturnValue(damage.getValue());
    }
}
