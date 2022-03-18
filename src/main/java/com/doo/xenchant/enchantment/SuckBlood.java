package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.EntityDamageApi;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;

/**
 * 吸血
 */
public class SuckBlood extends BaseEnchantment {

    public static final String NAME = "suck_blood";

    public SuckBlood() {
        super(NAME, Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
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
    public void register() {
        super.register();

        EntityDamageApi.ON_DAMAGED.register(((source, attacker, target, amount, map) -> {
            if (!Enchant.option.suckBlood) {
                return;
            }
            // need check stack
            ItemStack stack = attacker.getMainHandStack();
            int level = level(stack);
            if (level < 1 || level(stack = attacker.getOffHandStack()) < 1) {
                return;
            }

            if (!(stack.getItem() instanceof RangedWeaponItem || stack.getItem() instanceof SwordItem || stack.getItem() instanceof MiningToolItem)) {
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
            compound.putInt("id", attacker.getId());
            compound.putLong("age", attacker.age);

            // suck scale - if SWEEPING attack
            if (id == attacker.getId() && age == attacker.age) {
                level *= EnchantmentHelper.getLevel(Enchantments.SWEEPING, stack) > 0 ? 0.2F : 0.1F;
            }

            attacker.heal(level * amount / 10);
        }));
    }
}
