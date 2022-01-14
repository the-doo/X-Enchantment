package com.doo.xenchant.mixin;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LootPool.class)
public abstract class LootTableMixin {

    @Environment(EnvType.SERVER)
    @ModifyArg(method = "addGeneratedLoot", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/MathHelper;floor(F)I"))
    public float resetTotalLoot(LootContext context, float value) {
        if (Enchant.option.moreLoot) {
            return value * EnchantUtil.loot(context);
        }
        return value;
    }
}
