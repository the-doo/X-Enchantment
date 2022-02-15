package com.doo.xenchant.enchantment;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;

/**
 * 吸血
 */
public class SuckBlood extends BaseEnchantment {

    public static final String NAME = "suck_blood";

    public SuckBlood() {
        super(NAME, Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 1 + (level - 1) * 20;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof ToolItem ||
                stack.getItem() instanceof RangedWeaponItem ||
                stack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    @Override
    public void damageCallback(LivingEntity attacker, LivingEntity target, ItemStack stack, int level, float amount) {
        if (stack != attacker.getStackInHand(Hand.MAIN_HAND)) {
            return;
        }

        // try log
        String key = getId().toString();
        NbtCompound compound = stack.getOrCreateNbt();
        if (!compound.contains(key)) {
            compound.put(key, new NbtCompound());
        }
        compound = compound.getCompound(key);

        long id = compound.getInt("id");
        long age = compound.getLong("age");
        int count = compound.getInt("count");
        if (id == attacker.getId() && age >= attacker.age && count > 5) {
            return;
        }

        compound.putInt("id", attacker.getId());
        compound.putLong("age", attacker.age);

        // if change user or next attack
        if (id != attacker.getId() || age < attacker.age) {
            count = 0;
        }

        // suck scale
        float scale = level;
        if (count >= 1) {
            scale *= EnchantmentHelper.getLevel(Enchantments.SWEEPING, stack) > 0 ? 0.2F : 0.1F;
        }
        attacker.heal(scale * amount / 10);

        count++;
        compound.putInt("count", count);
    }
}
