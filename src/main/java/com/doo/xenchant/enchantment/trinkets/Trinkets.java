package com.doo.xenchant.enchantment.trinkets;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.enchantment.BaseEnchantment;
import com.doo.xenchant.events.AnvilApi;
import com.doo.xenchant.events.GrindApi;
import com.google.common.collect.Maps;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * It's trinkets enchantment, maybe you don't like it
 */
public class Trinkets extends BaseEnchantment {

    private static final String TS_KEY = "enchantment.x_enchant.trinkets.attr";

    private static final String NAME = "trinkets";

    private static final String FLAG = "Enchanted";

    private static final Map<String, Trinkets> KEY_MAP = Maps.newHashMap();

    private static final EntityAttributeModifier MODIFIER =
            new EntityAttributeModifier("trinkets", 1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

    private static final Text SMALL = new TranslatableText(TS_KEY + ".small");

    private static final Text MID = new TranslatableText(TS_KEY + ".mid");

    private static final Text LARGE = new TranslatableText(TS_KEY + ".large");

    private static boolean regis = false;


    private final EntityAttribute attr;

    public Trinkets(Attrs attrs) {
        super(NAME + "_-_" + attrs.attribute.getTranslationKey(), Rarity.COMMON, EnchantmentTarget.BREAKABLE, EquipmentSlot.values());

        attr = attrs.attribute;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public final Text getName(int level) {
        return new TranslatableText(getTranslationKey(), new TranslatableText(attr.getTranslationKey()).getString())
                .append(level == 1 ? SMALL : level == 2 ? MID : LARGE)
                .formatted(Formatting.GREEN);
    }

    @Override
    public String getTranslationKey() {
        return "enchantment.x_enchant.trinkets.attr";
    }

    @Override
    public final boolean isTreasure() {
        return true;
    }

    @Override
    public final boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof TrinketItem && !stack.hasEnchantments() || StringUtils.equals(stack.getOrCreateNbt().getString(nbtKey(FLAG)), getTranslationKey());
    }

    @Override
    protected final boolean canAccept(Enchantment other) {
        return false;
    }

    @Override
    public String nbtKey(String key) {
        return Enchant.ID + "_" + NAME + "_" + key;
    }

    @Override
    public void register() {
        super.register();

        KEY_MAP.put(attr.getTranslationKey(), this);

        if (regis) {
            return;
        }
        regis = true;

        // write modifier
        AnvilApi.ON_ENCHANT.register(((map, first, second, result) -> {
            if (!(result.getItem() instanceof TrinketItem)) {
                return;
            }

            map.entrySet().stream().filter(e -> e.getKey() instanceof Trinkets).findFirst().ifPresent(e -> {
                Enchantment enchantment = e.getKey();
                Integer level = e.getValue();
                NbtCompound nbt = result.getOrCreateNbt();
                if (!nbt.contains("TrinketAttributeModifiers", 9)) {
                    nbt.put("TrinketAttributeModifiers", new NbtList());
                }
                NbtList nbtList = nbt.getList("TrinketAttributeModifiers", 10);
                NbtCompound nbtCompound = MODIFIER.toNbt();
                nbtCompound.putDouble("Amount", MODIFIER.getValue() * (level == 1 ? 0.05 : level == 2 ? 0.1 : 0.25));
                nbtCompound.putString("AttributeName", Registry.ATTRIBUTE.getId(((Trinkets) enchantment).attr).toString());
                nbtList.add(nbtCompound);
                nbt.putString(nbtKey(FLAG), ((Trinkets) enchantment).attr.getTranslationKey());
            });
        }));

        // remove modifier
        GrindApi.ON_ENCHANT.register(((map, first, second, result) -> {
            if (!(result.getItem() instanceof TrinketItem)) {
                return;
            }

            NbtCompound nbt = result.getOrCreateNbt();
            Trinkets e = KEY_MAP.get(nbt.getString(nbtKey(FLAG)));
            if (e == null) {
                return;
            }

            if (!nbt.contains("TrinketAttributeModifiers", 9)) {
                nbt.put("TrinketAttributeModifiers", new NbtList());
            }
            NbtList nbtList = nbt.getList("TrinketAttributeModifiers", 10);
            nbtList.removeIf(n -> ((NbtCompound) n).getString("AttributeName").equals(Registry.ATTRIBUTE.getId(e.attr).toString()));
            nbt.remove(nbtKey(FLAG));
        }));
    }

    public enum Attrs {
        HEALTH(EntityAttributes.GENERIC_MAX_HEALTH),
        ARMOR(EntityAttributes.GENERIC_ARMOR),
        TOUGHNESS(EntityAttributes.GENERIC_ARMOR_TOUGHNESS),
        SPEED(EntityAttributes.GENERIC_MOVEMENT_SPEED),
        ATTACK(EntityAttributes.GENERIC_ATTACK_DAMAGE),
        ;

        public final EntityAttribute attribute;

        Attrs(EntityAttribute attribute) {
            this.attribute = attribute;
        }
    }
}
