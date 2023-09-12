package com.doo.xenchantment.enchantment.special;

import com.doo.xenchantment.enchantment.WithEffect;
import com.doo.xenchantment.interfaces.Usable;
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

public class Disenchantment extends Special implements Usable<Disenchantment> {

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
    public boolean onUsed(Integer level, ItemStack stack, Player player, InteractionHand hand, Consumer<InteractionResultHolder<ItemStack>> consumer) {
        ItemStack other = hand == InteractionHand.MAIN_HAND ? player.getOffhandItem() : player.getMainHandItem();
        if (other.isEmpty()) {
            return false;
        }

        boolean isBook = other.is(Items.ENCHANTED_BOOK);
        ListTag enchantedTag = isBook ? EnchantedBookItem.getEnchantments(other) : other.getEnchantmentTags();
        if (enchantedTag.isEmpty()) {
            return false;
        }

        ResourceLocation id = getId();
        ListTag bookTag = stack.is(Items.ENCHANTED_BOOK) ? EnchantedBookItem.getEnchantments(stack) : stack.getEnchantmentTags();
        bookTag.removeIf(t -> id.equals(EnchantmentHelper.getEnchantmentId((CompoundTag) t)));

        int len = Math.min(level, enchantedTag.size());
        for (int i = 0; i < len; i++) {
            Tag tag = enchantedTag.remove(0);
            bookTag.add(tag);

            WithEffect.removeIfEq((CompoundTag) tag, other);
        }
        if (isBook && enchantedTag.isEmpty()) {
            other.setCount(0);
        }

        consumer.accept(InteractionResultHolder.consume(stack));

        return true;
    }
}
