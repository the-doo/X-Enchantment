package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.EntityDamageApi;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ToolItem;

/**
 * 弱点攻击
 */
public class Weakness extends BaseEnchantment {

    public static final String NAME = "weakness";

    public Weakness() {
        super(NAME, Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 30;
    }

    @Override
    public int getMaxPower(int level) {
        return level * 50;
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

        EntityDamageApi.MULTIPLIER.register(((source, attacker, target, map) -> {
            if (map.isEmpty() || !Enchant.option.weakness) {
                return 0;
            }

            int level;
            ItemStack stack = attacker.getMainHandStack();
            if (stack.isEmpty() || (level = level(stack)) < 1) {
                return 0;
            }

            return attacker.getRandom().nextInt(100) < 5 * level ? 200 : 0;
        }));
    }
}
