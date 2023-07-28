package com.doo.xenchantment.enchantment.special;

import com.doo.xenchantment.events.AnvilApi;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

public class InfinityEnhance extends Special {

    public InfinityEnhance() {
        super("infinity_enhance", EnchantmentCategory.BOW, EquipmentSlot.values());
    }

    @Override
    public boolean canEnchant(@NotNull ItemStack stack) {
        return EnchantmentHelper.getEnchantments(stack).containsKey(Enchantments.INFINITY_ARROWS);
    }

    @Override
    protected boolean onlyOneLevel() {
        return true;
    }

    @Override
    public void onServer(MinecraftServer server) {
        AnvilApi.register(((player, map, first, second, result) -> {
            if (disabled()) {
                return;
            }

            if (second.is(Items.ENCHANTED_BOOK) && map.containsKey(this) && map.containsKey(Enchantments.INFINITY_ARROWS)) {
                // replace mending
                map.remove(this);
                map.put(Enchantments.MENDING, 1);
            }
        }));
    }
}
