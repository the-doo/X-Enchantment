package com.doo.xenchantment.enchantment.special;

import com.google.gson.JsonObject;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class GoBack extends Special {

    private static final String CONSUMER_KEY = "consumer";

    private static final String CD_KEY = "cd";

    private static final String LAST_KEY = "last";

    private static final Component WAIT_TIP = Component.translatable("x_enchantment.go_back.tips.wait");

    public GoBack() {
        super("go_back", EnchantmentCategory.BREAKABLE);

        options.addProperty(CD_KEY, 3);
        options.addProperty(CONSUMER_KEY, 100);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, CD_KEY);
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
        String key = nbtKey(LAST_KEY);
        long millis = Util.getEpochMillis();
        if (stack.getTag().getLong(key) >= millis) {
            player.displayClientMessage(WAIT_TIP, true);
            return false;
        }

        consumer.accept(InteractionResultHolder.consume(stack));
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
        player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 1.0f);

        if (!stack.isEmpty()) {
            stack.getTag().putLong(key, millis + (long) (1000 * doubleV(CD_KEY)));
        }

        return true;
    }
}
