package com.doo.xenchant.mixin;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.BiFunction;
import java.util.function.Consumer;

@Mixin(LootTable.class)
public abstract class LootTableMixin {

    @ModifyArg(method = "generateUnprocessedLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/function/LootFunction;apply(Ljava/util/function/BiFunction;Ljava/util/function/Consumer;Lnet/minecraft/loot/context/LootContext;)Ljava/util/function/Consumer;"), index = 1)
    public Consumer<ItemStack> forLoot2(BiFunction<ItemStack, LootContext, ItemStack> itemApplier, Consumer<ItemStack> lootConsumer, LootContext context) {
        if (Enchant.option.moreLoot) {
            int k = EnchantUtil.loot(context);
            if (k < 1) {
                return lootConsumer;
            }

            return lootConsumer.andThen(EnchantUtil.lootConsumer(k, lootConsumer));
        }
        return lootConsumer;
    }
}
