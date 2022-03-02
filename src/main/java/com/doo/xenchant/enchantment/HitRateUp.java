package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.PersistentApi;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

/**
 * 命中率提升
 */
public class HitRateUp extends BaseEnchantment {

    public static final String NAME = "hit_rate_up";

    public HitRateUp() {
        super(NAME, Rarity.UNCOMMON, EnchantmentTarget.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 20;
    }

    @Override
    public int getMaxPower(int level) {
        return level * 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public void register() {
        super.register();

        PersistentApi.ON_COLL.register(((owner, stack, world, pos, box) -> {
            if (!Enchant.option.hitRateUp) {
                return null;
            }

            int level = level(stack);
            if (level < 1) {
                return null;
            }

            return world.getOtherEntities(owner, box.expand(level), e -> e instanceof LivingEntity)
                    .stream().filter(e -> !e.isTeammate(owner) && e.squaredDistanceTo(pos) <= level).findFirst()
                    .orElse(null);
        }));
    }
}
