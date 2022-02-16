package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

/**
 * 重生
 */
public class Rebirth extends BaseEnchantment {

    public static final String NAME = "rebirth";

    public Rebirth() {
        super(NAME, Rarity.UNCOMMON, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMinPower(int level) {
        return level * 25;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 50;
    }

    @Override
    public void register() {
        super.register();

        // true is allow death
        ServerPlayerEvents.ALLOW_DEATH.register(((player, damageSource, damageAmount) -> {
            if (!Enchant.option.rebirth) {
                return true;
            }

            ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
            Rebirth rebirth = BaseEnchantment.get(Rebirth.class);

            int level = rebirth.level(stack);
            if (level < 1) {
                return true;
            }

            // use totem effect
            // see net.minecraft.entity.LivingEntity#tryUseTotem(net.minecraft.entity.damage.DamageSource)
            player.setHealth(player.getMaxHealth());
            player.clearStatusEffects();
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 500, 4));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 500, 4));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 500, 4));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 500, 2));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 500, 2));
            player.world.sendEntityStatus(player, (byte) 35);

            // decrement 1 level, see ItemStack.addEnchantment
            stack.getNbt().getList(ItemStack.ENCHANTMENTS_KEY, 10).stream()
                    .map(e -> (NbtCompound) e).filter(e -> EnchantmentHelper.getIdFromNbt(e).equals(rebirth.getId()))
                    .findFirst()
                    .ifPresent(e -> EnchantmentHelper.writeLevelToNbt(e, level - 1));
            return false;
        }));
    }
}
