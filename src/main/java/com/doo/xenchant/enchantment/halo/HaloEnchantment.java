package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.config.Option;
import com.doo.xenchant.enchantment.BaseEnchantment;
import com.doo.xenchant.mixin.interfaces.ServerLivingApi;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import org.apache.commons.lang3.mutable.MutableInt;

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
        super(NAME + "_" + name, Rarity.RARE, EnchantmentTarget.ARMOR, EnchantUtil.ALL_ARMOR);
    }

    @Override
    public final int getMinPower(int level) {
        return 20;
    }

    @Override
    public final int getMaxPower(int level) {
        return 50;
    }

    @Override
    protected final boolean canAccept(Enchantment other) {
        return !(other instanceof HaloEnchantment);
    }

    @Override
    public void register() {
        super.register();

        // only regis once
        if (!regis) {
            regis = true;

            ServerLivingApi.TAIL_TICK.register(living -> {
                if (!Enchant.option.halo) {
                    return;
                }

                if (Enchant.option.haloAllowOther == Option.AllowTarget.PLAYER && !(living instanceof ServerPlayerEntity)) {
                    return;
                }

                int age = living.age;
                Map<HaloEnchantment<?>, Integer> map = new HashMap<>();
                for (ItemStack stack : living.getArmorItems()) {
                    EnchantmentHelper.get(stack).forEach((k, v) -> {
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
                Box box = living.getBoundingBox().expand(Enchant.option.haloRange);
                map.forEach((k, v) -> k.halo(living, v, box));
            });
        }
    }

    /**
     * trigger halo
     */
    private void halo(LivingEntity living, Integer level, Box box) {
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

    protected abstract List<T> targets(LivingEntity living, Box box);

    protected abstract void onTarget(LivingEntity entity, Integer level, List<T> targets);
}
