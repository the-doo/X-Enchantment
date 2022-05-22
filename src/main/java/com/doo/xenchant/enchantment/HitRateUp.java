package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.PersistentApi;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 命中率提升
 */
public class HitRateUp extends BaseEnchantment {

    public static final String NAME = "hit_rate_up";

    public HitRateUp() {
        super(NAME, Rarity.UNCOMMON, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public void register() {
        super.register();

        PersistentApi.ON_COLL.register(((owner, stack, world, pos, box) -> {
            if (!Enchant.option.hitRateUp || owner == null) {
                return null;
            }

            int level = level(stack);
            if (level < 1) {
                return null;
            }

            return world.getEntities(owner, box.inflate(level), e -> e instanceof LivingEntity)
                    .stream().filter(e -> !e.isAlliedTo(owner) && e.distanceToSqr(pos) <= level).findFirst()
                    .orElse(null);
        }));
    }
}
