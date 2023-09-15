package com.doo.xenchantment.enchantment.halo;

import com.doo.playerinfo.core.InfoGroupItems;
import com.doo.xenchantment.enchantment.special.TimeFaster;
import com.doo.xenchantment.interfaces.FurnaceAccessor;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class BurnWell extends Halo {

    private static final String INC_KEY = "inc";

    protected BurnWell(EquipmentSlot slot) {
        super("burn_well", slot);
    }

    @Override
    public void initHaloFirstOptions() {
        options.addProperty(INTERVAL_KEY, 1.5);
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
        double inc = doubleV(INC_KEY) / 100;
        BlockPos.betweenClosedStream(box).forEach(p -> {
            BlockState state = level.getBlockState(p);
            if (TimeFaster.needFaster(p, state, level)) {
                return;
            }

            BlockEntity entity = level.getBlockEntity(p);
            if (entity instanceof AbstractFurnaceBlockEntity cb && cb.getContainerSize() > 0 && FurnaceAccessor.canBurn(cb, level)) {
                FurnaceAccessor.inc(cb, inc);
            }
        });
    }
}
