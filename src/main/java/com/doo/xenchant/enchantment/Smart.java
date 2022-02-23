package com.doo.xenchant.enchantment;

import com.doo.xenchant.mixin.interfaces.ServerLivingApi;
import com.doo.xenchant.util.EnchantUtil;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * Smart
 */
public class Smart extends BaseEnchantment {

    public static final String NAME = "smart";

    public static final MutableText EPIPHANY = new TranslatableText("enchantment.x_enchant.chat.smart")
            .setStyle(Style.EMPTY.withColor(Formatting.GOLD));

    public Smart() {
        super(NAME, Rarity.VERY_RARE, EnchantmentTarget.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinPower(int level) {
        return 30;
    }

    @Override
    public int getMaxPower(int level) {
        return level * getMinPower(level);
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public void register() {
        super.register();

        ServerLivingApi.TAIL_TICK.register(living -> {
            if (!(living instanceof ServerPlayerEntity) || living.age % (SECOND * 5) != 0) {
                return;
            }

            ItemStack stack = living.getEquippedStack(EquipmentSlot.HEAD);
            if (stack.isEmpty()) {
                return;
            }

            int level = level(stack);
            if (level < 1) {
                return;
            }

            // add level xp
            int amount = level;
            // if epiphany - 0.0005
            if (living.getRandom().nextInt(1000) < 5) {
                amount *= 1000;
                EnchantUtil.sendMessage((ServerPlayerEntity) living, living.getDisplayName(), EPIPHANY);
            }

            ((PlayerEntity) living).addExperience(amount);
        });
    }
}
