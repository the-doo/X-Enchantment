package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.enchantment.halo.LivingHalo;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Diffusion
 */
public class Diffusion extends BaseEnchantment {

    public static final String NAME = "diffusion";

    public Diffusion() {
        super(NAME, Rarity.RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ProjectileWeaponItem;
    }

    @Override
    public void doPostAttack(@NotNull LivingEntity user, @NotNull Entity target, int level) {
        if (target instanceof LivingEntity) {
            DamageSource source = DamageSource.mobAttack(user);
            float damage = (float) (Enchant.option.diffusionDamage + user.getRandom().nextInt((int) (level * Enchant.option.diffusionDamage)));
            Predicate<LivingEntity> test = e -> LivingHalo.Type.HARMFUL.predicate.and((u, t) -> t != target).test(user, e);

            if (target.level instanceof ServerLevel) {
                ((ServerLevel) target.level).sendParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getY(), target.getZ(), 30, 2, -2, 2, 1);
                target.level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(3), test).forEach(e -> e.hurt(source, damage));
            }
        }
    }
}
