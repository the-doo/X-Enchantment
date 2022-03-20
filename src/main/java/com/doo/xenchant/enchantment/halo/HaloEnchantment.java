package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.config.Option;
import com.doo.xenchant.enchantment.BaseEnchantment;
import com.doo.xenchant.events.LivingApi;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enchantment of Halo
 */
public abstract class HaloEnchantment<T extends Entity> extends BaseEnchantment {

    public static final String NAME = "halo";

    public static boolean regis = false;

    public HaloEnchantment(String name) {
        super(NAME + "_" + name, Rarity.RARE, EnchantmentCategory.ARMOR, EnchantUtil.ALL_ARMOR);
    }

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment enchantment) {
        return !(enchantment instanceof HaloEnchantment);
    }

    @Override
    public void register() {
        super.register();

        // only regis once
        if (regis) {
            return;
        }
        regis = true;

        LivingApi.SEVER_TAIL_TICK.register(living -> {
            if (!Enchant.option.halo) {
                return;
            }

            if (Enchant.option.haloAllowOther == Option.AllowTarget.PLAYER && !(living instanceof ServerPlayer)) {
                return;
            }

            int age = living.tickCount;
            Map<HaloEnchantment<?>, Integer> map = new HashMap<>();
            for (ItemStack stack : living.getArmorSlots()) {
                EnchantmentHelper.getEnchantments(stack).forEach((k, v) -> {
                    if (k instanceof HaloEnchantment && v != null && v > 0) {
                        HaloEnchantment<?> halo = (HaloEnchantment<?>) k;
                        if (!halo.ban(living) && age % (halo.triggerTime() * SECOND) == 0) {
                            map.compute(halo, (k1, v1) -> v1 == null ? v : v1 + v);
                        }
                    }
                });
            }
            if (map.isEmpty()) {
                return;
            }

            // trigger match halo
            AABB box = living.getBoundingBox().inflate(Enchant.option.haloRange);
            map.forEach((k, v) -> k.halo(living, v, box));
        });
    }

    /**
     * trigger halo
     */
    private void halo(LivingEntity living, Integer level, AABB box) {
        List<T> targets = targets(living, box);

        if (targets != null && !targets.isEmpty()) {
            onTarget(living, level, targets);
        }
    }

    protected boolean ban(LivingEntity living) {
        return false;
    }

    protected float triggerTime() {
        return 1;
    }

    protected abstract List<T> targets(LivingEntity living, AABB box);

    protected abstract void onTarget(LivingEntity entity, Integer level, List<T> targets);
}
