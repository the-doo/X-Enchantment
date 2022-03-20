package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * 重生
 */
public class Rebirth extends BaseEnchantment {

    public static final String NAME = "rebirth";

    public Rebirth() {
        super(NAME, Rarity.UNCOMMON, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public void register() {
        super.register();

        // true is allow death
        ServerPlayerEvents.ALLOW_DEATH.register(((player, damageSource, damageAmount) -> {
            if (!Enchant.option.rebirth) {
                return true;
            }

            ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);

            int level = level(stack);
            if (level < 1) {
                return true;
            }

            // use totem effect
            // see net.minecraft.entity.LivingEntity#tryUseTotem(net.minecraft.entity.damage.DamageSource)
            player.setHealth(player.getMaxHealth());
            player.removeAllEffects();
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 500, 4));
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 500, 4));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 500, 4));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 500, 2));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 500, 2));
            player.level.broadcastEntityEvent(player, (byte) 35);

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
        }));
    }
}
