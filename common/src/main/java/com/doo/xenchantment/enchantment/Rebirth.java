package com.doo.xenchantment.enchantment;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * 重生
 */
public class Rebirth extends BaseXEnchantment {

    public Rebirth() {
        super("rebirth", Rarity.RARE, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});

        options.addProperty(MAX_LEVEL_KEY, 5);
    }

    @Override
    public boolean canDeath(LivingEntity living) {
        if (disabled() || !(living instanceof Player)) {
            return true;
        }

        ItemStack stack = living.getItemBySlot(EquipmentSlot.CHEST);
        int level = level(stack);
        if (level < 1) {
            return true;
        }

        // use totem effect
        // see net.minecraft.entity.LivingEntity#tryUseTotem(net.minecraft.entity.damage.DamageSource)
        living.setHealth(living.getMaxHealth());
        living.removeAllEffects();
        living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 500, 4));
        living.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 500, 4));
        living.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 500, 4));
        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 500, 2));
        living.addEffect(new MobEffectInstance(MobEffects.JUMP, 500, 2));
        living.level().broadcastEntityEvent(living, (byte) 35);

        // decrement 1 level, see ItemStack.addEnchantment
        ListTag list = stack.getOrCreateTag().getList(ItemStack.TAG_ENCH, Tag.TAG_COMPOUND);
        list.stream().filter(e -> getId().equals(EnchantmentHelper.getEnchantmentId((CompoundTag) e)))
                .findFirst().ifPresent(e -> {
                    if (level - 1 < 1) {
                        list.remove(e);
                        return;
                    }
                    EnchantmentHelper.setEnchantmentLevel((CompoundTag) e, level - 1);
                });
        return false;
    }
}
