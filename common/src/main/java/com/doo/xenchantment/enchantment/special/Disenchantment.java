package com.doo.xenchantment.enchantment.special;

import com.doo.xenchantment.enchantment.WithEffect;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.function.Consumer;

public class Disenchantment extends Special {

    public Disenchantment() {
        super("disenchantment", EnchantmentCategory.BREAKABLE);

        options.addProperty(MAX_LEVEL_KEY, 5);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);
    }

    @Override
    public boolean canEnchant(ItemStack itemStack) {
        return false;
    }

    @Override
    protected boolean checkCompatibility(Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canUsed() {
        return true;
    }

    @Override
    public boolean onUsed(Integer level, ItemStack stack, Player player, InteractionHand hand, Consumer<InteractionResultHolder<ItemStack>> consumer) {
        ItemStack other = hand == InteractionHand.MAIN_HAND ? player.getOffhandItem() : player.getMainHandItem();
        if (other.isEmpty()) {
            return false;
        }

        ListTag enchantedTag = other.is(Items.ENCHANTED_BOOK) ? EnchantedBookItem.getEnchantments(other) : other.getEnchantmentTags();
        if (enchantedTag.isEmpty()) {
            return false;
        }

        ResourceLocation id = getId();
        ListTag bookTag = stack.is(Items.ENCHANTED_BOOK) ? EnchantedBookItem.getEnchantments(stack) : stack.getEnchantmentTags();

        int len = Math.min(level + 1, enchantedTag.size());
        for (int i = 0; i < len; i++) {
            Tag tag = enchantedTag.remove(i);
            bookTag.add(tag);

            WithEffect.removeIfEq((CompoundTag) tag, other);
        }
        bookTag.removeIf(t -> id.equals(EnchantmentHelper.getEnchantmentId((CompoundTag) t)));

        consumer.accept(InteractionResultHolder.success(stack));
        return true;
    }
}
