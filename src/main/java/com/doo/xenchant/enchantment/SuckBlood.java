package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.EntityDamageApi;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * 吸血
 */
public class SuckBlood extends BaseEnchantment {

    public static final String NAME = "suck_blood";

    public SuckBlood() {
        super(NAME, Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isTradeable() {
        return true;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof TieredItem ||
                stack.getItem() instanceof ProjectileWeaponItem ||
                stack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public void register() {
        super.register();

        EntityDamageApi.ON_DAMAGED.register(((source, attacker, target, amount, map) -> {
            if (!Enchant.option.suckBlood) {
                return;
            }
            // need check stack
            ItemStack stack = attacker.getMainHandItem();
            int level = level(stack);
            if (level < 1 && level(stack = attacker.getOffhandItem()) < 1) {
                return;
            }

            if (!(stack.getItem() instanceof ProjectileWeaponItem || stack.getItem() instanceof SwordItem || stack.getItem() instanceof DiggerItem)) {
                return;
            }

            // try log
            String key = getId().toString();
            CompoundTag compound = stack.getOrCreateTag();
            if (!compound.contains(key)) {
                compound.put(key, new CompoundTag());
            }
            compound = compound.getCompound(key);

            long id = compound.getInt("id");
            long age = compound.getLong("age");
            compound.putInt("id", attacker.getId());
            compound.putLong("age", attacker.tickCount);

            // suck scale - if SWEEPING attack
            if (id == attacker.getId() && age == attacker.tickCount) {
                level *= EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SWEEPING_EDGE, stack) > 0 ? 0.2F : 0.1F;
            }

            attacker.heal(level * amount / 10);
        }));
    }
}
