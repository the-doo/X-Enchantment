package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.XPlayerInfo;
import com.doo.playerinfo.core.InfoGroupItems;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

/**
 * Night Break
 * <p>
 * from @NightBreak
 */
public class NightBreak extends BaseXEnchantment {
    public static final String LOG_DAMAGE_KEY = "DamageCount";
    public static final String DAMAGE_KEY = "damage";
    public static final String DAMAGE_COUNT_KEY = "damage_count";
    public static final String TIP_KEY = "tip";

    private static final MutableComponent THANKS = Component.literal(" - ").append(Component.translatable("enchantment.x_enchantment.night_break.tips")).withStyle(ChatFormatting.DARK_GRAY);

    public NightBreak() {
        super("night_break", Rarity.VERY_RARE, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);

        options.addProperty(MAX_LEVEL_KEY, 5);
        options.addProperty(DAMAGE_KEY, 2);
        options.addProperty(DAMAGE_COUNT_KEY, 2);
        options.addProperty(TIP_KEY, true);
    }

    @Override
    public @NotNull Component getFullname(int level) {
        if (getBoolean(TIP_KEY)) {
            super.getFullname(level);
        }
        return super.getFullname(level).copy().append(THANKS);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, DAMAGE_KEY);
        loadIf(json, DAMAGE_COUNT_KEY);
        loadIf(json, TIP_KEY);
    }

    @Override
    public void doPostAttack(LivingEntity living, Entity entity, int level) {
        if (living.level().isClientSide() || living == entity || level < 1) {
            return;
        }
        if (!(entity instanceof LivingEntity e) || !LogHitTick.canHit(e)) {
            return;
        }

        ItemStack stack = living.getMainHandItem();
        CompoundTag tag = stack.getOrCreateTag();
        String log = nbtKey(LOG_DAMAGE_KEY);

        int count = tag.getInt(log);
        if (count++ >= getDouble(DAMAGE_COUNT_KEY)) {
            count = 0;
            float hurt = (float) (getDouble(DAMAGE_KEY) * level * e.getMaxHealth() / 100);
            e.heal(-hurt);
        }

        tag.putInt(log, count);
    }

    @Override
    public InfoGroupItems collectPlayerInfo(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        int level = level(stack);
        InfoGroupItems group = InfoGroupItems.groupKey(getDescriptionId());
        group.add(getInfoKey(DAMAGE_COUNT_KEY), getDouble(DAMAGE_COUNT_KEY), false);
        group.add(getInfoKey(DAMAGE_KEY), level < 1 ? 0 : getDouble(DAMAGE_KEY) * level / 100, true);
        return group;
    }

    public interface LogHitTick {

        static boolean canHit(Object o) {
            return ((LogHitTick) XPlayerInfo.get(o)).canHit();
        }

        boolean canHit();
    }
}
