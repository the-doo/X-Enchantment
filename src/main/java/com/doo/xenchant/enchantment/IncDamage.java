package com.doo.xenchant.enchantment;

import com.doo.xenchant.events.EntityDamageApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * Increment Attack Damage
 */
public class IncDamage extends BaseEnchantment {

    public static final String NAME = "increment_attack_damage";

    private static final String KEY = "Damages";

    public IncDamage() {
        super(NAME, Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 50;
    }

    @Override
    public int getMaxPower(int level) {
        return level * 150;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof ToolItem;
    }

    @Override
    public void register() {
        super.register();

        // DamageApi
        EntityDamageApi.ADD.register((((source, attacker, target, map) -> {
            ItemStack stack;
            if (!map.containsKey(this) || (stack = attacker.getMainHandStack()).isEmpty() || level(stack) < 1) {
                return 0;
            }

            return stack.getOrCreateNbt().getFloat(nbtKey(KEY));
        })));

        // inc value when killed other
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(((world, entity, killedEntity) -> {
            if (!(entity instanceof LivingEntity)) {
                return;
            }

            // check level
            ItemStack stack = ((LivingEntity) entity).getMainHandStack();
            int level;
            if (stack.isEmpty() || (level = level(stack)) < 1) {
                return;
            }

            ToolItem item = (ToolItem) stack.getItem();

            NbtCompound compound = stack.getOrCreateNbt();
            float now = compound.getFloat(nbtKey(KEY));
            float max = 0;
            if (item instanceof SwordItem) {
                max = ((SwordItem) item).getAttackDamage();
            } else if (item instanceof MiningToolItem) {
                max = ((MiningToolItem) item).getAttackDamage();
            }
            max *= level;

            if (now >= max) {
                return;
            }

            // inc = random scale * inc()
            float inc = ((LivingEntity) entity).getRandom().nextFloat() * inc(item.getMaterial().getDurability());
            if (inc > 0) {
                compound.putFloat(nbtKey(KEY), Math.min(max, now + inc));
            }
        }));

        // tooltips
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ItemTooltipCallback.EVENT.register((ItemStack stack, TooltipContext context, List<Text> lines) -> {
                if (level(stack) > 0) {
                    lines.add(new TranslatableText(getTranslationKey())
                            .append(": ↑")
                            .append(FORMAT.format(stack.getOrCreateNbt().getFloat(nbtKey(KEY))))
                            .formatted(Formatting.GRAY));
                }
            });
        }
    }

    /**
     * Default DIAMOND is 1 base
     *
     * @param durability tool durability
     * @return inc value
     */
    private float inc(int durability) {
        return 1F * durability / ToolMaterials.DIAMOND.getDurability();
    }
}
