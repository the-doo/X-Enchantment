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
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
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

    private static final Component LOG_TIPS = Component.translatable("x_enchantment.tp_to_player.tips.mark");
    private static final Component ONE_TIPS = Component.translatable("x_enchantment.tp_to_player.tips.only");
    private static final Component NOT_SELF_TIPS = Component.translatable("x_enchantment.tp_to_player.tips.me");
    private static final Component OFF_LINE_TIPS = Component.translatable("x_enchantment.tp_to_player.tips.offline");
    private static final Component NO_PLAYER_TIPS = Component.translatable("x_enchantment.tp_to_player.tips.check");
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
    public boolean onUsed(Integer level, ItemStack stack, Player player, InteractionHand hand, Consumer<InteractionResultHolder<ItemStack>> consumer) {
        if (stack.getEnchantmentTags().size() > 1) {
            player.displayClientMessage(ONE_TIPS, true);
            return false;
        }

        String uuidKey = nbtKey(UUID_KEY);
        if (player.getPose() == Pose.CROUCHING) {
            return logPlayer(player, other -> {
                stack.getTag().putString(uuidKey, other.getStringUUID());
                stack.getTag().putString(nbtKey(NAME_KEY), other.getDisplayName().getString());
                consumer.accept(InteractionResultHolder.consume(stack));
            });
        }

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

    private boolean logPlayer(Player player, Consumer<Player> callback) {
        Vec3 vec3 = player.getViewVector(1.0f).normalize();

        Player other;
        for (Entity entity : player.level().getEntities(player, player.getBoundingBox().inflate(5, 1, 5), e -> e instanceof Player)) {
            other = (Player) entity;
            Vec3 vec32 = new Vec3(other.getX() - player.getX(), other.getEyeY() - player.getEyeY(), other.getZ() - player.getZ());
            double d = vec32.length();
            double e = vec3.dot(vec32.normalize());
            if (e > 1.0 - 0.025 / d) {
                callback.accept(other);
                return true;
            }
        }

        player.displayClientMessage(NO_PLAYER_TIPS, true);
        return false;
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