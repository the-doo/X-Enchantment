package com.doo.xenchantment.enchantment;

import com.doo.xenchantment.interfaces.Tooltipsable;
import com.doo.xenchantment.interfaces.WithAttribute;
import com.google.gson.JsonObject;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class IncDamage extends BaseXEnchantment implements
        WithAttribute<IncDamage>, Tooltipsable<IncDamage> {

    private static final java.util.UUID[] UUID = {
            java.util.UUID.fromString("4DD34D4C-9B52-4674-85F4-B9569BAABFFC")
    };

    private static final String KEY = "Damages";
    private static final String REDUCE_KEY = "reduce";
    private static final String PER_LEVEL_DAMAGE_KEY = "damage_per_level";
    private static final List<Attribute> ATTRIBUTES = Collections.singletonList(Attributes.ATTACK_DAMAGE);

    public IncDamage() {
        super("increment_attack_damage", Rarity.VERY_RARE, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);

        options.addProperty(MAX_LEVEL_KEY, 4);
        options.addProperty(PER_LEVEL_DAMAGE_KEY, 1.5);
        options.addProperty(REDUCE_KEY, 50);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, PER_LEVEL_DAMAGE_KEY);
        loadIf(json, REDUCE_KEY);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof TieredItem;
    }

    @Override
    public List<java.util.UUID[]> getUUIDs() {
        return Collections.singletonList(UUID);
    }

    @Override
    public List<Attribute> getAttribute() {
        return ATTRIBUTES;
    }

    @Override
    public AttributeModifier getMatchModify(Attribute attribute, ItemStack stack, int level) {
        float damage = stack.getOrCreateTag().getFloat(nbtKey(KEY));
        float reduce = (float) (doubleV(REDUCE_KEY) / 100);
        if (stack.getItem() instanceof SwordItem si) {
            damage -= si.getDamage() * reduce;
        } else if (stack.getItem() instanceof DiggerItem di) {
            damage -= di.getAttackDamage() * reduce;
        }
        return oneAttrModify(stackIdx(stack, slots), 1, damage * 100, AttributeModifier.Operation.ADDITION);
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
        float max;
        if (isSGItem(compound)) {
            max = sgAttack(compound);
        } else if (ti instanceof SwordItem si) {
            max = si.getDamage();
        } else if (ti instanceof DiggerItem di) {
            max = di.getAttackDamage();
        } else {
            return;
        }
        max *= (float) (level * doubleV(PER_LEVEL_DAMAGE_KEY));

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

    // sg like
    private boolean isSGItem(CompoundTag tag) {
        return tag.contains("SGear_Data") &&
                Optional.ofNullable(tag.getCompound("SGear_Data")).map(c ->
                        Optional.ofNullable(c.getCompound("Properties")).map(c1 ->
                                        Optional.ofNullable(c1.getCompound("Stats"))
                                                .map(c2 -> c2.contains("silentgear:melee_damage"))
                                                .orElse(false)).orElse(false)).orElse(false);
    }

    private float sgAttack(CompoundTag tag) {
        return tag.getCompound("SGear_Data").getCompound("Properties")
                .getCompound("Stats").getFloat("silentgear:melee_damage");
    }

}
