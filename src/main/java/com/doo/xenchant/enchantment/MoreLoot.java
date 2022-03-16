package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.LootApi;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Wearable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * 更多战利品
 */
public class MoreLoot extends BaseEnchantment {

    public static final String NAME = "more_loot";

    public MoreLoot() {
        super(NAME, Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return !(stack.getItem() instanceof Wearable);
    }

    @Override
    public void register() {
        super.register();

        LootApi.HANDLER.register(((killer, stack, baseConsumer, context) -> {
            if (!Enchant.option.moreLoot || killer.isDead() || killer.world.isClient()) {
                return null;
            }

            int level = level(stack);
            if (level < 1) {
                return null;
            }

            // no effect on
            BlockState block = context.get(LootContextParameters.BLOCK_STATE);
            if (block != null && stack.getItem().isSuitableFor(block)) {
                return null;
            }

            int rand = rand(level, context.getRandom());
            if (rand < 1) {
                return null;
            }

            Consumer<ItemStack> dropper = getDropper(killer, context);
            return i -> {
                // if is block item, need return
                if (i.getItem() instanceof BlockItem) {
                    return i;
                }

                // Add level xp
                if (killer instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity) killer).addExperience(rand);
                }

                if (!i.isStackable()) {
                    // isn't stackable only half
                    IntStream.range(0, Math.max(level, rand / 5)).forEach(v -> dropper.accept(i.copy()));
                    return i;
                }

                int max = i.getMaxCount();
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

        // rand min: 1 ~ level * 1.5
        return Math.max(1, (int) (random.nextInt(level) + level / 2F));
    }

    private Consumer<ItemStack> getDropper(LivingEntity living, LootContext context) {
        BlockState state = context.get(LootContextParameters.BLOCK_STATE);
        if (state != null) {
            Vec3d vec3d = context.get(LootContextParameters.ORIGIN);
            return vec3d == null ? living::dropStack : i -> state.onStacksDropped((ServerWorld) living.getWorld(), new BlockPos(vec3d), i);
        }

        Entity e = context.get(LootContextParameters.THIS_ENTITY);
        return e != living && e != null ? e::dropStack : living::dropStack;
    }
}
