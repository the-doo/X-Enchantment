package com.doo.xenchant.enchantment.curse;

import com.doo.xenchant.events.ItemApi;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

/**
 * Thin
 */
public class Thin extends Cursed {

    public static final String NAME = "thin";

    public Thin() {
        super(NAME, Rarity.COMMON, EnchantmentTarget.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public int getMinPower(int level) {
        return 1 + (level - 1) * 10;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void register() {
        super.register();

        ItemApi.WILL_DAMAGE.register(((owner, stack, amount) -> {
            if (owner == null) {
                return;
            }

            int level = level(stack);
            if (level < 1) {
                return;
            }

            if (owner.getRandom().nextInt(100) < 25) {
                stack.setDamage(stack.getDamage() + level * 2);
            }
        }));
    }
}
