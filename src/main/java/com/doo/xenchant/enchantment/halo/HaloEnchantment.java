package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.attribute.LimitTimeModifier;
import com.doo.xenchant.enchantment.BaseEnchantment;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 光环类附魔
 */
public abstract class HaloEnchantment extends BaseEnchantment {

    public static final String NAME = "halo";

    public static final List<EntityAttribute> ATTRIBUTES = new ArrayList<>(16);

    private final boolean isFriendTarget;

    public HaloEnchantment(String name, boolean isFriendTarget) {
        super(NAME + "_" + name, Rarity.RARE, EnchantmentTarget.ARMOR, EnchantUtil.ALL_ARMOR);

        this.isFriendTarget = isFriendTarget;
    }

    public static boolean isHalo(String id) {
        return BaseEnchantment.isBase(id) && id.contains(NAME);
    }

    @Override
    public final int getMaxLevel() {
        return 1;
    }

    @Override
    public final int getMinPower(int level) {
        return 10;
    }

    @Override
    public final int getMaxPower(int level) {
        return 25;
    }

    @Override
    protected final boolean canAccept(Enchantment other) {
        return !(other instanceof HaloEnchantment && this.isTreasure() && other.isTreasure());
    }

    @Override
    public void livingTick(LivingEntity living, ItemStack stack, int level) {
        if (!Enchant.option.halo) {
            return;
        }

        Iterable<ItemStack> armor = living.getArmorItems();

        Map<String, Integer> counter = new HashMap<>();
        Map<String, Integer> min = new HashMap<>();
        MutableInt total = new MutableInt(0);

        armor.forEach(i -> {
            total.increment();
            i.getEnchantments().forEach(n -> {
                // id and lvl can see EnchantmentHelper.ID_KEY/EnchantmentHelper.LEVEL_KEY
                String id = EnchantUtil.id(n);
                int lvl = EnchantUtil.lvl(n);
                if (lvl > 0 && HaloEnchantment.isHalo(id)) {
                    counter.compute(id, (k, v) -> v == null ? 1 : v + 1);
                    min.compute(id, (k, v) -> v == null ? lvl : Math.min(lvl, v));
                }
            });
        });
        counter.keySet().removeIf(k -> counter.getOrDefault(k, 0) < total.intValue());
        if (counter.isEmpty()) {
            return;
        }

        // get targets
        Box box = living.getBoundingBox().expand(Enchant.option.haloRange);
        Map<Boolean, List<LivingEntity>> entities = living.world.getNonSpectatingEntities(LivingEntity.class, box)
                .stream().collect(Collectors.groupingBy(e -> e == living || e.isTeammate(living)));

        // trigger halo
        counter.keySet().stream().map(k -> (HaloEnchantment) get(k)).forEach(k -> k.halo(living, min.get(k), entities::get));
    }

    /**
     * trigger halo
     *
     * @param player        玩家
     * @param level         等级
     * @param targetsGetter 目标获取器
     */
    public final void halo(LivingEntity player, Integer level, Function<Boolean, List<LivingEntity>> targetsGetter) {
        if (!needTick()) {
            return;
        }

        List<LivingEntity> targets = targetsGetter.apply(isFriendTarget);
        if (targets != null && !targets.isEmpty()) {
            onTarget(player, level, targets);
        }
    }

    protected abstract boolean needTick();

    public abstract void onTarget(LivingEntity entity, Integer level, List<LivingEntity> targets);

    /**
     * 添加或更新修改值
     *
     * @param attr     修改的属性
     * @param modifier 修改值 默认值
     */
    public void addOrResetModifier(EntityAttributeInstance attr, LimitTimeModifier modifier) {
        Optional<EntityAttributeModifier> optional = attr.getModifiers().stream()
                .filter(m -> m.getName().equals(getId().toString()) && m instanceof LimitTimeModifier).findAny();
        if (optional.isPresent()) {
            ((LimitTimeModifier) optional.get()).reset(1.2F);
        } else {
            attr.addTemporaryModifier(modifier);
        }
    }
}
