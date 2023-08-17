package com.doo.xenchantment.enchantment.curse;

import com.doo.xenchantment.XEnchantment;
import com.doo.xenchantment.advancements.TrueTrigger;
import com.doo.xenchantment.interfaces.WithAttribute;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.AABB;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class Insanity extends Cursed implements WithAttribute<Insanity> {
    private static final UUID[] UUIDS = {
            UUID.fromString("DD097C02-AF3B-BBEE-EA80-52576EE3253F")
    };
    public static final TrueTrigger HIT =
            TrueTrigger.get(new ResourceLocation(XEnchantment.MOD_ID, "trigger.insanity.hit"));
    private static final String VALUE_KEY = "value";
    private static final String ATTACK_INTERVAL_KEY = "attack_interval";
    private static final String ATTACK_RATE_KEY = "attack_rate";
    private static final String ATTACK_RANGE_KEY = "attack_range";
    private static final List<Attribute> ATTRIBUTES = Collections.singletonList(XEnchantment.rangeAttr());

    public Insanity() {
        super("insanity", Rarity.RARE, EnchantmentCategory.ARMOR_HEAD, EquipmentSlot.HEAD, EquipmentSlot.HEAD);

        options.addProperty(VALUE_KEY, 2);
        options.addProperty(ATTACK_INTERVAL_KEY, 3);
        options.addProperty(ATTACK_RATE_KEY, 50);
        options.addProperty(ATTACK_RANGE_KEY, 3);
    }

    @Override
    protected boolean onlyOneLevel() {
        return true;
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, VALUE_KEY);
        loadIf(json, ATTACK_INTERVAL_KEY);
        loadIf(json, ATTACK_RATE_KEY);
        loadIf(json, ATTACK_RANGE_KEY);
    }

    @Override
    public List<UUID[]> getUUIDs() {
        return Collections.singletonList(UUIDS);
    }

    @Override
    public List<Attribute> getAttribute() {
        return ATTRIBUTES;
    }

    @Override
    public AttributeModifier getMatchModify(Attribute attribute, ItemStack stack, int level) {
        return oneAttrModify(stackIdx(stack, slots), 1, doubleV(VALUE_KEY) * 100, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void onEndTick(LivingEntity living) {
        if (!(living instanceof Player p) || living.tickCount % (int) (SECOND_TICK * doubleV(ATTACK_INTERVAL_KEY)) != 0) {
            return;
        }

        if (level(living.getItemBySlot(EquipmentSlot.HEAD)) < 1) {
            return;
        }

        if (doubleV(ATTACK_RATE_KEY) / 100 > living.getRandom().nextDouble()) {
            return;
        }

        double range = doubleV(ATTACK_RANGE_KEY);
        AABB aabb = living.getBoundingBox().inflate(range, 0.5, range);
        living.level().getEntitiesOfClass(LivingEntity.class, aabb, e -> e != living)
                .stream().findAny().ifPresent(e -> {
                    p.attack(e);
                    HIT.trigger((ServerPlayer) p);
                });
    }

    @Override
    public boolean hasAdv() {
        return true;
    }

    @Override
    public TrueTrigger getAdvTrigger() {
        return HIT;
    }
}
