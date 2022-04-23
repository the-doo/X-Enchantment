package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.text.DecimalFormat;

/**
 * 附魔基类
 */
public abstract class BaseEnchantment extends Enchantment {

    static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    /**
     * 1s is 20 ticks
     */
    protected static final int SECOND = 20;

    private static final ChatFormatting[] RATE_COLOR = {ChatFormatting.GRAY, ChatFormatting.BLUE, ChatFormatting.YELLOW, ChatFormatting.GOLD};

    private final ResourceLocation id;

    protected BaseEnchantment(String name, Rarity weight, EnchantmentCategory type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
        this.id = new ResourceLocation(Enchant.ID, name);
    }

    @Override
    public Component getFullname(int level) {
        Component name = super.getFullname(level);
        return isCurse() ? name : name.copy().withStyle(RATE_COLOR[getRarity().ordinal()]);
    }

    @Override
    public int getMinCost(int level) {
        switch (getRarity()) {
            case UNCOMMON:
                return 25;
            case RARE:
                return level * 35;
            case VERY_RARE:
                return level * 50;
            default:
                return super.getMinCost(level);
        }
    }

    @Override
    public int getMaxCost(int level) {
        switch (getRarity()) {
            case UNCOMMON:
                return getMinCost(level) + 35;
            case RARE:
                return getMinCost(level) + 50;
            case VERY_RARE:
                return getMinCost(level) + 100;
            default:
                return super.getMaxCost(level);
        }
    }

    @Override
    public boolean isTradeable() {
        return getRarity() == Rarity.VERY_RARE || getRarity() == Rarity.RARE;
    }

    public ResourceLocation getId() {
        return id;
    }

    public String nbtKey(String key) {
        return id.toString() + key;
    }

    public int level(ItemStack item) {
        return item == null || item.isEmpty() ? 0 : EnchantmentHelper.getEnchantments(item).getOrDefault(this, 0);
    }

    public final int second(float second) {
        return (int) (SECOND * second);
    }

    /**
     * Can regis to any event or other things
     */
    public void register() {
        // Don't replace if exist
        if (Registry.ENCHANTMENT.containsKey(id)) {
            return;
        }

        Registry.register(Registry.ENCHANTMENT, id, this);
    }
}
