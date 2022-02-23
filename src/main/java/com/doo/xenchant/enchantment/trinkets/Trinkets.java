package com.doo.xenchant.enchantment.trinkets;

import com.doo.xenchant.enchantment.BaseEnchantment;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

/**
 * It's trinkets enchantment, maybe you don't like it
 */
public abstract class Trinkets extends BaseEnchantment {

    protected Trinkets(String name) {
        super(name, Rarity.UNCOMMON, EnchantmentTarget.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public final Text getName(int level) {
        return super.getName(level).shallowCopy().formatted(Formatting.GREEN);
    }

    @Override
    public final boolean isTreasure() {
        return true;
    }

    @Override
    public final boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof TrinketItem && !stack.hasEnchantments();
    }

    @Override
    protected final boolean canAccept(Enchantment other) {
        return false;
    }

    public final void writeModifier(ItemStack item) {
        EntityAttributeModifier modifier = getModifier();
        if (modifier == null) {
            return;
        }
        EntityAttribute attribute = getAttribute();
        if (attribute == null) {
            return;
        }

        NbtCompound nbt = item.getOrCreateNbt();
        if (!nbt.contains("TrinketAttributeModifiers", 9)) {
            nbt.put("TrinketAttributeModifiers", new NbtList());
        }
        NbtList nbtList = nbt.getList("TrinketAttributeModifiers", 10);
        NbtCompound nbtCompound = modifier.toNbt();
        nbtCompound.putString("AttributeName", Registry.ATTRIBUTE.getId(attribute).toString());
        nbtList.add(nbtCompound);
    }

    public final void removeModifier(ItemStack item) {
        EntityAttributeModifier modifier = getModifier();
        if (modifier == null) {
            return;
        }
        EntityAttribute attribute = getAttribute();
        if (attribute == null) {
            return;
        }

        NbtCompound nbt = item.getOrCreateNbt();
        if (!nbt.contains("TrinketAttributeModifiers", 9)) {
            nbt.put("TrinketAttributeModifiers", new NbtList());
        }
        NbtList nbtList = nbt.getList("TrinketAttributeModifiers", 10);
        nbtList.removeIf(n -> ((NbtCompound) n).getString("AttributeName").equals(Registry.ATTRIBUTE.getId(attribute).toString()));
    }

    public EntityAttribute getAttribute() {
        return null;
    }

    public EntityAttributeModifier getModifier() {
        return null;
    }
}
