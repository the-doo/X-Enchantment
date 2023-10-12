package com.doo.xenchantment.enchantment.special;

import com.doo.xenchantment.interfaces.BlockEntityAccessor;
import com.doo.xenchantment.interfaces.Usable;
import com.doo.xenchantment.util.EnchantUtil;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class TimeFaster extends Special implements Usable<TimeFaster> {

    private static final String PER_SPEED_KEY = "per_speed";
    private static final String MAX_SPEED_KEY = "max_speed";
    private static final String SYNC_KEY = "sync";
    private static final Component NOT_EXIST = Component.translatable("x_enchantment.time_faster.tips.not_exist");
    private static final Component MAX_LIMIT = Component.translatable("x_enchantment.time_faster.tips.max_limit");

    private static final ExecutorService EXECUTORS = Executors.newFixedThreadPool(4);

    public TimeFaster() {
        super("time_faster", EnchantmentCategory.BREAKABLE);

        options.addProperty(PER_SPEED_KEY, 10);
        options.addProperty(MAX_SPEED_KEY, 10);
        options.addProperty(SYNC_KEY, true);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, PER_SPEED_KEY);
        loadIf(json, MAX_SPEED_KEY);
        loadIf(json, SYNC_KEY);
    }

    public boolean canEnchant(ItemStack itemStack) {
        return false;
    }

    public boolean checkCompatibility(Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean useOnBlock(Integer value, ItemStack stack, Player player, InteractionHand hand,
                              BlockState state, Block block, BlockEntity entity, Consumer<InteractionResult> consumer) {
        if (entity == null) {
            player.displayClientMessage(NOT_EXIST, true);
            return false;
        }

        int speed = BlockEntityAccessor.timeSpeed(entity);
        int max = (int) (doubleV(MAX_SPEED_KEY) * 10);
        if (speed >= max) {
            player.displayClientMessage(MAX_LIMIT, true);
            return false;
        }

        BlockEntityAccessor.timeSpeed(entity, value * intV(PER_SPEED_KEY), max);
        if (!player.isCreative()) {
            stack.setCount(0);
        }
        consumer.accept(InteractionResult.CONSUME);
        return true;
    }

    public static boolean needFaster(BlockPos pos, BlockState state, ServerLevel level) {
        TimeFaster e = (TimeFaster) EnchantUtil.ENCHANTMENTS_MAP.get(TimeFaster.class);
        if (e.disabled()) {
            return false;
        }

        return e.faster(pos, state, level);
    }

    public boolean faster(BlockPos pos, BlockState state, ServerLevel level) {
        BlockEntity block = level.getBlockEntity(pos);
        if (block == null || block.isRemoved() || !block.hasLevel()) {
            return false;
        }

        BlockEntityTicker<BlockEntity> ticker = (BlockEntityTicker<BlockEntity>) state.getTicker(level, block.getType());
        if (ticker == null) {
            return false;
        }

        int times = BlockEntityAccessor.timeSpeed(block) * 100;
        if (times < 1) {
            return false;
        }

        execute(() -> {
            for (int i = 0; i < times; i++) {
                ticker.tick(level, pos, state, block);
            }
        });
        return true;
    }

    private void execute(Runnable run) {
        if (boolV(SYNC_KEY)) {
            run.run();
        } else {
            EXECUTORS.execute(run);
        }
    }
}
