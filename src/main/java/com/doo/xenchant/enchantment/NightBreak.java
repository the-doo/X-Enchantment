package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.EntityDamageApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Night Break
 * <p>
 * from @NightBreak
 */
public class NightBreak extends BaseEnchantment {

    public static final String NAME = "night_break";
    private static final String KEY = "Count";

    private static final Component TIPS = new TranslatableComponent("enchantment.x_enchant.night_break.tips");

    public NightBreak() {
        super(NAME, Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return Enchant.option.nightBreakMaxLevel;
    }

    @Override
    public void register() {
        super.register();

        EntityDamageApi.REAL_ADD.register((((source, attacker, target, map, targetMap) -> {
            if (!(attacker instanceof LivingEntity) || !map.containsKey(this) || !Enchant.option.nightBreakIsReal) {
                return 0;
            }

            return hurtValue((LivingEntity) attacker, target);
        })));

        EntityDamageApi.ADD.register((((source, attacker, target, map, targetMap) -> {
            if (!(attacker instanceof LivingEntity) || !map.containsKey(this) || Enchant.option.nightBreakIsReal) {
                return 0;
            }

            return hurtValue((LivingEntity) attacker, target);
        })));

        // tooltips
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ItemTooltipCallback.EVENT.register(((stack, context, lines) -> {
                if (stack.getItem() instanceof EnchantedBookItem) {
                    return;
                }

                if (level(stack) > 0) {
                    lines.add(new TranslatableComponent(getDescriptionId()).append(": ").append(stack.getOrCreateTag().getLong(nbtKey(KEY)) + "").withStyle(ChatFormatting.GRAY));
                    lines.add(new TranslatableComponent(getDescriptionId()).append(" - ").append(TIPS).withStyle(ChatFormatting.GRAY));
                }
            }));
        }
    }

    private float hurtValue(LivingEntity attacker, LivingEntity target) {
        ItemStack stack = attacker.getMainHandItem();
        if (stack.isEmpty()) {
            return 0;
        }

        int level = level(stack);
        if (level < 1) {
            return 0;
        }

        CompoundTag nbt = stack.getOrCreateTag();

        long count = nbt.getLong(nbtKey(KEY));
        nbt.putLong(nbtKey(KEY), count += 1);

        // if attack 3 times, damage is level * 10% * maxHealth
        if (count % 3 == 0) {
            nbt.putLong(nbtKey(KEY), 0);
            return (float) (target.getMaxHealth() * level / (Enchant.option.nightBreakPerLevel / 100));
        }

        return 0;
    }
}
