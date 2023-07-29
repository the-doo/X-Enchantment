package com.doo.xenchantment.enchantment.halo;

import com.doo.xenchantment.interfaces.CropBlockAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;

public class FarmSpeed extends Halo {

    protected FarmSpeed(EquipmentSlot slot) {
        super("farm_speed", Rarity.RARE, slot);
    }

    @Override
    protected void initHaloFirstOptions() {
        options.addProperty(INTERVAL_KEY, 3);
    }

    @Override
    protected void trigger(LivingEntity living, AABB box) {
        ServerLevel level = (ServerLevel) living.level();
        BlockPos.betweenClosedStream(box).forEach(p -> {
            BlockState state = level.getBlockState(p);
            if (!(state.getBlock() instanceof CropBlock cb) || cb.isMaxAge(state)) {
                return;
            }

            IntegerProperty property = CropBlockAccessor.get((CropBlock) state.getBlock());
            level.setBlock(p, state.setValue(property, Math.min(state.getValue(property) + 3, cb.getMaxAge())), 2);
        });
    }
}
