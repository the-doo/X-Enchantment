package com.doo.xenchantment.enchantment.special;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class GoBack extends Special {

    private static final String CONSUMER_KEY = "consumer";

    public GoBack() {
        super("go_back", EnchantmentCategory.BREAKABLE);

        options.addProperty(CONSUMER_KEY, 100);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, CONSUMER_KEY);
    }

    @Override
    public boolean canEnchant(ItemStack itemStack) {
        return false;
    }

    @Override
    protected boolean checkCompatibility(Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canUsed() {
        return true;
    }

    @Override
    public boolean onUsed(Integer level, ItemStack stack, Player player, InteractionHand hand, Consumer<InteractionResultHolder<ItemStack>> consumer) {
        consumer.accept(InteractionResultHolder.success(stack));
        ListTag tag = EnchantedBookItem.getEnchantments(stack);
        if (!player.isCreative() && doubleV(CONSUMER_KEY) / 100 > player.getRandom().nextDouble()) {
            resetLevel(level, stack, tag);
        }

        // teleport
        ServerPlayer serverPlayer = (ServerPlayer) player;
        ServerLevel serverlevel = Optional.ofNullable(serverPlayer.server.getLevel(serverPlayer.getRespawnDimension()))
                .orElse(serverPlayer.server.overworld());
        BlockPos pos = Optional.ofNullable(serverPlayer.getRespawnPosition())
                .filter(b -> serverlevel.getBlockState(b).is(BlockTags.BEDS))
                .orElse(serverlevel.getSharedSpawnPos());
        player.teleportTo(serverlevel, pos.getX(), pos.getY(), pos.getZ(), Set.of(), serverPlayer.getYRot(), serverPlayer.getXRot());
        serverlevel.playSound(null, serverPlayer.xo, serverPlayer.yo, serverPlayer.zo, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0f, 1.0f);

        return true;
    }

    private void resetLevel(Integer level, ItemStack stack, ListTag tag) {
        if (level == 1) {
            tag.removeIf(this::isSameId);
            if (tag.isEmpty()) {
                stack.setCount(0);
            }
        } else {
            tag.stream().filter(this::isSameId).findFirst()
                    .ifPresent(t -> EnchantmentHelper.setEnchantmentLevel((CompoundTag) t, level - 1));
        }
    }
}
