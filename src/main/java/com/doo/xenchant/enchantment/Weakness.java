package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.EntityDamageApi;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 弱点攻击
 */
public class Weakness extends BaseEnchantment {

    public static final String NAME = "weakness";

    public Weakness() {
        super(NAME, Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
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

        EntityDamageApi.MULTIPLIER.register(((source, attacker, target, map, targetMap) -> {
            if (!Enchant.option.weakness || !(attacker instanceof LivingEntity) || !map.containsKey(this)) {
                return 0;
            }

            int level;
            ItemStack stack = ((LivingEntity) attacker).getMainHandItem();
            if ((level = level(stack)) < 1) {
                return 0;
            }

            return ((LivingEntity) attacker).getRandom().nextInt(100) < Enchant.option.weaknessChance * level ? 200 : 0;
        }));
    }
}
