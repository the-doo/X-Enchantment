package com.doo.xenchant.enchantment.trinkets;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.enchantment.BaseEnchantment;
import com.doo.xenchant.events.AnvilApi;
import com.doo.xenchant.events.GrindApi;
import com.google.common.collect.Maps;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * It's trinkets enchantment, maybe you don't like it
 */
public class Trinkets extends BaseEnchantment {

    private static final String TS_KEY = "enchantment.x_enchant.trinkets.attr";

    private static final String NAME = "trinkets";

    private static final String FLAG = "Enchanted";

    private static final Map<String, Trinkets> KEY_MAP = Maps.newHashMap();

    private static final Component SMALL = new TranslatableComponent(TS_KEY + ".small");

    private static final Component MID = new TranslatableComponent(TS_KEY + ".mid");

    private static final Component LARGE = new TranslatableComponent(TS_KEY + ".large");

    private static boolean regis = false;


    private final Attribute attr;

    public Trinkets(Attrs attrs) {
        super(NAME + "_-_" + attrs.attribute.getDescriptionId(), Rarity.UNCOMMON, EnchantmentCategory.BREAKABLE, EquipmentSlot.values());

        attr = attrs.attribute;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public final Component getFullname(int level) {
        return new TranslatableComponent(getDescriptionId(), new TranslatableComponent(attr.getDescriptionId()).getString())
                .append(level == 1 ? SMALL : level == 2 ? MID : LARGE)
                .withStyle(ChatFormatting.GREEN);
    }

    @Override
    public String getDescriptionId() {
        return "enchantment.x_enchant.trinkets.attr";
    }

    @Override
    public final boolean isTradeable() {
        return true;
    }

    @Override
    public boolean isDiscoverable() {
        return super.isDiscoverable();
    }

    @Override
    public final boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof TrinketItem && !stack.isEnchanted() || StringUtils.equals(stack.getOrCreateTag().getString(nbtKey(FLAG)), getDescriptionId());
    }

    @Override
    protected final boolean checkCompatibility(@NotNull Enchantment other) {
        return false;
    }

    @Override
    public String nbtKey(String key) {
        return Enchant.ID + "_" + NAME + "_" + key;
    }

    @Override
    public void register() {
        super.register();

        KEY_MAP.put(attr.getDescriptionId(), this);

        if (regis) {
            return;
        }
        regis = true;

        // write modifier
        AnvilApi.ON_ENCHANT.register(((player, map, first, second, result) -> {
            if (!(result.getItem() instanceof TrinketItem)) {
                return;
            }

            map.entrySet().stream().filter(e -> e.getKey() instanceof Trinkets).findFirst().ifPresent(e -> {
                Enchantment enchantment = e.getKey();
                Integer level = e.getValue();
                CompoundTag nbt = result.getOrCreateTag();
                if (!nbt.contains("TrinketAttributeModifiers", 9)) {
                    nbt.put("TrinketAttributeModifiers", new ListTag());
                }
                ListTag nbtList = nbt.getList("TrinketAttributeModifiers", 10);

                double value = level == 1 ? 0.05 : level == 2 ? 0.1 : 0.25;
                CompoundTag nbtCompound = new AttributeModifier("trinkets", value, AttributeModifier.Operation.MULTIPLY_TOTAL).save();
                nbtCompound.putDouble("Amount", value);
                nbtCompound.putString("AttributeName", Registry.ATTRIBUTE.getKey(((Trinkets) enchantment).attr).toString());
                nbtList.add(nbtCompound);
                nbt.putString(nbtKey(FLAG), ((Trinkets) enchantment).attr.getDescriptionId());
            });
        }));

        // remove modifier
        GrindApi.ON_ENCHANT.register(((map, first, second, result) -> {
            if (!(result.getItem() instanceof TrinketItem)) {
                return;
            }

            CompoundTag nbt = result.getOrCreateTag();
            Trinkets e = KEY_MAP.get(nbt.getString(nbtKey(FLAG)));
            if (e == null) {
                return;
            }

            if (!nbt.contains("TrinketAttributeModifiers", 9)) {
                nbt.put("TrinketAttributeModifiers", new ListTag());
            }
            ListTag nbtList = nbt.getList("TrinketAttributeModifiers", 10);
            Optional.ofNullable(Registry.ATTRIBUTE.getKey(e.attr))
                    .ifPresent(k -> nbtList.removeIf(n -> k.toString().equals(((CompoundTag) n).getString("AttributeName"))));
            nbt.remove(nbtKey(FLAG));
        }));
    }

    public enum Attrs {
        HEALTH(Attributes.MAX_HEALTH),
        ARMOR(Attributes.ARMOR),
        TOUGHNESS(Attributes.ARMOR_TOUGHNESS),
        SPEED(Attributes.MOVEMENT_SPEED),
        ATTACK(Attributes.ATTACK_DAMAGE),
        ;

        public final Attribute attribute;

        Attrs(Attribute attribute) {
            this.attribute = attribute;
        }
    }
}
