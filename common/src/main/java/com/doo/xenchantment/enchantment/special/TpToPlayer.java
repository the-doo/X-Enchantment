package com.doo.xenchantment.enchantment.special;

import com.doo.xenchantment.interfaces.OneLevelMark;
import com.doo.xenchantment.interfaces.Tooltipsable;
import com.doo.xenchantment.interfaces.Usable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class TpToPlayer extends Special implements
        Tooltipsable<TpToPlayer>, Usable<TpToPlayer>, OneLevelMark {
    private static final String UUID_KEY = "uuid";
    private static final String NAME_KEY = "name";
    private static final String TICK_KEY = "tick";

    private static final Component LOG_TIPS = Component.translatable("x_enchantment.tp_to_player.tips.mark");
    private static final Component ONE_TIPS = Component.translatable("x_enchantment.tp_to_player.tips.only");
    private static final Component NOT_SELF_TIPS = Component.translatable("x_enchantment.tp_to_player.tips.me");
    private static final Component OFF_LINE_TIPS = Component.translatable("x_enchantment.tp_to_player.tips.offline");
    private static final MutableComponent PLAYER_NAME_TIPS = Component.translatable("x_enchantment.tp_to_player.tips.marked").append(": ");

    public TpToPlayer() {
        super("tp_to_player", EnchantmentCategory.WEARABLE);
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
    public boolean useOnPlayer(Integer level, ItemStack stack, Player player, InteractionHand hand, Player target, Consumer<InteractionResult> consumer) {
        if (stack.getEnchantmentTags().size() > 1) {
            player.displayClientMessage(ONE_TIPS, true);
            return false;
        }

        if (player.getPose() != Pose.CROUCHING) {
            return false;
        }

        stack.getTag().putInt(nbtKey(TICK_KEY), player.tickCount);
        stack.getTag().putString(nbtKey(UUID_KEY), target.getStringUUID());
        stack.getTag().putString(nbtKey(NAME_KEY), target.getDisplayName().getString());
        consumer.accept(InteractionResult.CONSUME);
        return true;
    }

    @Override
    public boolean onUsed(Integer level, ItemStack stack, Player player, InteractionHand hand, Consumer<InteractionResultHolder<ItemStack>> consumer) {
        String uuidKey = nbtKey(UUID_KEY);
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(uuidKey)) {
            player.displayClientMessage(LOG_TIPS, true);
            return false;
        }

        String uuid = tag.getString(uuidKey);
        if (player.getStringUUID().equals(uuid)) {
            player.displayClientMessage(NOT_SELF_TIPS, true);
            return false;
        }

        String key = nbtKey(TICK_KEY);
        if (tag.getInt(key) == player.tickCount) {
            tag.remove(key);
            return false;
        }

        ServerPlayer other = player.getServer().getPlayerList().getPlayer(UUID.fromString(uuid));
        if (other == null) {
            player.displayClientMessage(OFF_LINE_TIPS, true);
            return false;
        }

        Vec3 pos = other.getPosition(0);
        player.teleportTo((ServerLevel) other.level(), pos.x(), pos.y(), pos.z(), Set.of(), player.getYRot(), player.getXRot());
        player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 1.0f);

        if (!player.isCreative()) {
            resetLevel(level, stack, stack.getEnchantmentTags());
        }

        consumer.accept(InteractionResultHolder.consume(stack));
        return true;
    }

    @Override
    public void tooltip(ItemStack stack, TooltipFlag context, List<Component> lines) {
        if (!stack.hasTag()) {
            return;
        }

        CompoundTag tag = stack.getTag();
        if (!tag.contains(nbtKey(UUID_KEY))) {
            return;
        }

        lines.add(PLAYER_NAME_TIPS.copy().append(tag.getString(nbtKey(NAME_KEY))).withStyle(ChatFormatting.DARK_GRAY));
    }
}