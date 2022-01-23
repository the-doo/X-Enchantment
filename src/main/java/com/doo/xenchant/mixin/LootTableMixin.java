package com.doo.xenchant.mixin;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(LootPool.class)
public abstract class LootTableMixin {

    @Shadow
    protected abstract void supplyOnce(Consumer<ItemStack> lootConsumer, LootContext context);

    @Inject(method = "addGeneratedLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootPool;supplyOnce(Ljava/util/function/Consumer;Lnet/minecraft/loot/context/LootContext;)V"))
    public void forLootI(Consumer<ItemStack> lootConsumer, LootContext context, CallbackInfo ci) {
        if (Enchant.option.moreLoot) {
            for (int k = EnchantUtil.loot(context); k > 0; k--) {
                supplyOnce(lootConsumer, context);
            }
        }
    }
}
