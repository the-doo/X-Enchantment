package com.doo.xenchant.enchantment;

import com.doo.xenchant.events.EntityDamageApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Increment Attack Damage
 */
public class IncDamage extends BaseEnchantment {

    public static final String NAME = "increment_attack_damage";

    private static final String KEY = "Damages";

    public IncDamage() {
        super(NAME, Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof TieredItem;
    }

    @Override
    public void register() {
        super.register();

        // DamageApi
        EntityDamageApi.ADD.register((((source, attacker, target, map, targetMap) -> {
            ItemStack stack;
            if (!(attacker instanceof LivingEntity) || !map.containsKey(this) || (stack = ((LivingEntity) attacker).getMainHandItem()).isEmpty() || level(stack) < 1) {
                return 0;
            }

            return stack.getOrCreateTag().getFloat(nbtKey(KEY));
        })));

        // inc value when killed other
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(((world, killer, killedEntity) -> {
            if (!(killer instanceof LivingEntity)) {
                return;
            }

            // check level
            ItemStack stack = ((LivingEntity) killer).getMainHandItem();
            int level;
            if (stack.isEmpty() || (level = level(stack)) < 1) {
                return;
            }

            TieredItem item = (TieredItem) stack.getItem();

            CompoundTag compound = stack.getOrCreateTag();
            float now = compound.getFloat(nbtKey(KEY));
            float max = 0;
            if (item instanceof SwordItem) {
                max = ((SwordItem) item).getDamage();
            } else if (item instanceof DiggerItem) {
                max = ((DiggerItem) item).getAttackDamage();
            }
            max *= level;

            if (now >= max) {
                return;
            }

            // inc = random scale * inc()
            float inc = ((LivingEntity) killer).getRandom().nextFloat() * inc(item.getTier().getUses());
            inc += killedEntity.getMaxHealth() / ((LivingEntity) killer).getMaxHealth() / 10;
            if (inc > 0) {
                compound.putFloat(nbtKey(KEY), Math.min(max, now + inc));
            }
        }));

        // tooltips
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
                if (level(stack) > 0) {
                    lines.add(new TranslatableComponent(getDescriptionId())
                            .append(": ↑")
                            .append(FORMAT.format(stack.getOrCreateTag().getFloat(nbtKey(KEY))))
                            .withStyle(ChatFormatting.GRAY));
                }
            });
        }
    }

    /**
     * Default DIAMOND is 0.5 base
     *
     * @param durability tool durability
     * @return inc value
     */
    private float inc(int durability) {
        return .5F * durability / Tiers.DIAMOND.getUses();
    }
}
