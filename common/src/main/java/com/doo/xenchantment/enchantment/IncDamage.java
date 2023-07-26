package com.doo.xenchantment.enchantment;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Increment Attack Damage
 */
public class IncDamage extends BaseXEnchantment {

    public static final java.util.UUID UUID = java.util.UUID.fromString("4DD34D4C-9B52-4674-85F4-B9569BAABFFC");

    private static final String KEY = "Damages";

    public IncDamage() {
        super("increment_attack_damage", Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});

        options.addProperty(MAX_LEVEL_KEY, 5);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof TieredItem;
    }

    @Override
    public boolean hasAttr() {
        return true;
    }

    @Override
    protected void modifiedAttrMap(ItemStack stack, int level, BiConsumer<Attribute, AttributeModifier> modifier) {
        float damage = stack.getOrCreateTag().getFloat(nbtKey(KEY));
        modifier.accept(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID, name, damage, AttributeModifier.Operation.ADDITION));
    }

    @Override
    public void onKilled(ServerLevel world, LivingEntity killer, LivingEntity killedEntity) {
        if (disabled() || killer == null) {
            return;
        }

        // check level
        ItemStack stack = killer.getMainHandItem();
        int level;
        if (stack.isEmpty() || !(stack.getItem() instanceof TieredItem ti) || (level = level(stack)) < 1) {
            return;
        }

        CompoundTag compound = stack.getOrCreateTag();
        float now = compound.getFloat(nbtKey(KEY));
        float max = 0;
        if (ti instanceof SwordItem si) {
            max = si.getDamage();
        } else if (ti instanceof DiggerItem di) {
            max = di.getAttackDamage();
        }
        max *= level;

        if (now >= max) {
            return;
        }

        // inc = random scale * inc()
        float inc = killer.getRandom().nextFloat() * inc(ti.getTier().getUses());
        inc += killedEntity.getMaxHealth() / killer.getMaxHealth() / 10;
        if (inc > 0) {
            compound.putFloat(nbtKey(KEY), Math.min(max, now + inc));
        }
    }

    @Override
    public boolean needTooltips() {
        return true;
    }

    @Override
    public void tooltip(ItemStack stack, TooltipFlag context, List<Component> lines) {
        if (level(stack) > 0 && !(stack.getItem() instanceof EnchantedBookItem)) {
            lines.add(Component.translatable(getDescriptionId())
                    .append(": +")
                    .append(FORMAT.format(stack.getOrCreateTag().getFloat(nbtKey(KEY))))
                    .withStyle(ChatFormatting.GRAY));
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