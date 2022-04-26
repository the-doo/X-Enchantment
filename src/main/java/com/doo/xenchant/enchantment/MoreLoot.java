package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.LootApi;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.function.Consumer;

/**
 * 更多战利品
 */
public class MoreLoot extends BaseEnchantment {

    public static final String NAME = "more_loot";

    public MoreLoot() {
        super(NAME, Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return !(stack.getItem() instanceof Wearable);
    }

    @Override
    public void register() {
        super.register();

        LootApi.HANDLER.register(((killer, stack, baseConsumer, context) -> {
            if (!Enchant.option.moreLoot || killer.isDeadOrDying() || killer.level.isClientSide()) {
                return null;
            }

            int level = level(stack);
            if (level < 1) {
                return null;
            }

            // no effect on
            BlockState block = context.getParamOrNull(LootContextParams.BLOCK_STATE);
            if (block != null && !stack.getItem().isCorrectToolForDrops(block)) {
                return null;
            }

            int rand = rand(level, context.getRandom());
            if (rand < 1) {
                return null;
            }

            Consumer<ItemStack> dropper = getDropper(killer, context);
            return i -> {
                // if is block item, need return
                if (i.getItem() instanceof BlockItem || !i.isStackable()) {
                    return i;
                }

                int max = i.getMaxStackSize();
                int count = i.getCount() * (1 + rand);
                if (count <= max) {
                    i.setCount(count);
                    return i;
                }
                i.setCount(max);
                count -= max;

                // need new one
                for (; count > 0; count -= max) {
                    ItemStack copy = i.copy();
                    copy.setCount(Math.min(count, max));
                    dropper.accept(copy);
                }
                return i;
            };
        }));
    }

    private int rand(int level, Random random) {
        // more loot chance
        int ran = random.nextInt(100);
        if (ran >= Enchant.option.moreLootRate - 1) {
            return 0;
        }

        // 1% only 0
        if (ran < Enchant.option.moreMoreLootRate) {
            level *= Enchant.option.moreMoreLootMultiplier;
        }

        // rand min: level / 2 ~ level * 1.5
        return Math.max(level / 2, (int) (random.nextInt(level) + level / 2F));
    }

    private Consumer<ItemStack> getDropper(LivingEntity living, LootContext context) {
        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (state != null) {
            Vec3 vec3d = context.getParamOrNull(LootContextParams.ORIGIN);
            return vec3d == null ? living::spawnAtLocation : i -> state.spawnAfterBreak((ServerLevel) living.level, new BlockPos(vec3d), i);
        }

        Entity e = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        return e != living && e != null ? e::spawnAtLocation : living::spawnAtLocation;
    }
}
