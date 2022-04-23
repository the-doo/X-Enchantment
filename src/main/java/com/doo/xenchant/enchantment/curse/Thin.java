package com.doo.xenchant.enchantment.curse;

import com.doo.xenchant.events.ItemApi;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Thin
 */
public class Thin extends Cursed {

    public static final String NAME = "thin";

    public Thin() {
        super(NAME, Rarity.COMMON, EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
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
                stack.setDamageValue(stack.getDamageValue() + level * 2);
            }
        }));
    }
}
