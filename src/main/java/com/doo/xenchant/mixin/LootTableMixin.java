package com.doo.xenchant.mixin;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(LootPool.class)
public abstract class LootTableMixin {

    @Shadow
    @Final
    private LootTableRange rolls;

    @Inject(at = @At(value = "HEAD"), method = "addGeneratedLoot")
    public void addGeneratedLootH(Consumer<ItemStack> lootConsumer, LootContext context, CallbackInfo ci) {
        if (Enchant.option.moreLoot) {
            EnchantUtil.moreLoot(context, this.rolls);
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "addGeneratedLoot")
    public void addGeneratedLootT(Consumer<ItemStack> lootConsumer, LootContext context, CallbackInfo ci) {
        if (Enchant.option.moreLoot) {
            EnchantUtil.resetLoot(this.rolls);
        }
    }
}
