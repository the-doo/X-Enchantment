package com.doo.xenchant.mixin;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(LootTable.class)
public abstract class LootTableMixin {

    @Final
    @Shadow
    LootPool[] pools;

    @Inject(method = "generateUnprocessedLoot", at = @At(value = "TAIL"))
    public void forLootI(LootContext context, Consumer<ItemStack> lootConsumer, CallbackInfo ci) {
        if (Enchant.option.moreLoot) {
            int k = EnchantUtil.loot(context);
            for (; k > 0; k--) {
                for (LootPool lootPool : pools) {
                    lootPool.addGeneratedLoot(lootConsumer, context);
                }
            }
        }
    }
}
