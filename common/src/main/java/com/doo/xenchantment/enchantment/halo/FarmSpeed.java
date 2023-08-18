package com.doo.xenchantment.enchantment.halo;

import com.doo.playerinfo.core.InfoGroupItems;
import com.doo.xenchantment.interfaces.IntegerPropertyAccessor;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;

public class FarmSpeed extends Halo {

    private static final String INC_KEY = "inc";

    protected FarmSpeed(EquipmentSlot slot) {
        super("farm_speed", slot);
    }

    @Override
    protected void initHaloFirstOptions() {
        options.addProperty(INC_KEY, 50);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, INC_KEY);
    }

    @Override
    protected void collectHaloPlayerInfo(ServerPlayer player, InfoGroupItems group) {
        group.add(getInfoKey(INC_KEY), doubleV(INC_KEY) / 100, true);
    }

    @Override
    protected void trigger(LivingEntity living, AABB box) {
        ServerLevel level = (ServerLevel) living.level();
        double v = doubleV(INC_KEY) / 100;
        BlockPos.betweenClosedStream(box).forEach(p -> {
            BlockState state = level.getBlockState(p);
            if (state.isAir() || !(state.is(BlockTags.CROPS) || state.is(Blocks.FARMLAND) || state.getBlock() instanceof CocoaBlock)) {
                return;
            }

            state.getProperties().forEach(property -> {
                if (!(property instanceof IntegerProperty ip)) {
                    return;
                }

                int max = IntegerPropertyAccessor.max(property);
                int current = state.getValue(ip);
                if (max <= current) {
                    return;
                }

                current += Math.max(1, (int) (max * v));
                level.setBlock(p, state.setValue(ip, Math.min(current, max)), 2);
            });

            if (state.getBlock() instanceof StemBlock) {
                state.randomTick(level, p, level.random);
            }
        });
    }
}
