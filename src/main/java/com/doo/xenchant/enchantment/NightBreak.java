package com.doo.xenchant.enchantment;

import com.doo.xenchant.events.EntityDamageApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * Night Break
 * <p>
 * from @NightBreak
 */
public class NightBreak extends BaseEnchantment {

    public static final String NAME = "night_break";
    private static final String KEY = "Count";

    private static final Text TIPS = new TranslatableText("enchantment.x_enchant.night_break.tips");

    public NightBreak() {
        super(NAME, Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public void register() {
        super.register();

        EntityDamageApi.REAL_ADD.register((((source, attacker, target, map) -> {
            if (!map.containsKey(this)) {
                return 0;
            }

            ItemStack stack = attacker.getMainHandStack();
            if (stack.isEmpty()) {
                return 0;
            }

            int level = level(stack);
            if (level < 1) {
                return 0;
            }

            NbtCompound nbt = stack.getOrCreateNbt();

            long count = nbt.getLong(nbtKey(KEY));
            nbt.putLong(nbtKey(KEY), count += 1);

            // if attack 3 times, damage is level * 10% * maxHealth
            if (count % 3 == 0) {
                nbt.putLong(nbtKey(KEY), 0);
                return target.getMaxHealth() * level / 10;
            }

            return 0;
        })));

        // tooltips
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ItemTooltipCallback.EVENT.register(((stack, context, lines) -> {
                if (level(stack) > 0) {
                    lines.add(new TranslatableText(getTranslationKey()).append(": ").append(stack.getOrCreateNbt().getLong(nbtKey(KEY)) + "").formatted(Formatting.GRAY));
                    lines.add(new TranslatableText(getTranslationKey()).append(" - ").append(TIPS).formatted(Formatting.GRAY));
                }
            }));
        }
    }
}
