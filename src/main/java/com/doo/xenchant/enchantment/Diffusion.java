package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.enchantment.halo.LivingHalo;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.function.Predicate;

/**
 * Diffusion
 */
public class Diffusion extends BaseEnchantment {

    public static final String NAME = "diffusion";

    public Diffusion() {
        super(NAME, Rarity.RARE, EnchantmentTarget.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 100;
    }

    @Override
    public int getMaxPower(int level) {
        return level * getMaxLevel();
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof RangedWeaponItem;
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if (target instanceof LivingEntity) {
            DamageSource source = DamageSource.mob(user);
            float damage = (float) (Enchant.option.diffusionDamage + user.getRandom().nextInt((int) (level * Enchant.option.diffusionDamage)));
            Predicate<LivingEntity> test = e -> LivingHalo.Type.HARMFUL.predicate.and((u, t) -> t != target).test(user, e);

            if (target.world instanceof ServerWorld) {
                ((ServerWorld) target.world).spawnParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getY(), target.getZ(), 30, 2, -2, 2, 1);
            }
            target.world.getEntitiesByClass(LivingEntity.class, target.getBoundingBox().expand(3), test).forEach(e -> e.damage(source, damage));
        }
    }
}
