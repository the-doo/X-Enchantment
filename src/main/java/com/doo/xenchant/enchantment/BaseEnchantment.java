package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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

    private static final Formatting[] RATE_COLOR = {Formatting.GRAY, Formatting.BLUE, Formatting.YELLOW, Formatting.GOLD};

    private final Identifier id;

    protected BaseEnchantment(String name, Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
        this.id = new Identifier(Enchant.ID, name);
    }

    @Override
    public Text getName(int level) {
        Text name = super.getName(level);
        return isCursed() ? name : name.shallowCopy().formatted(RATE_COLOR[getRarity().ordinal()]);
    }

    @Override
    public int getMinPower(int level) {
        switch (getRarity()) {
            case UNCOMMON:
                return 25;
            case RARE:
                return level * 25;
            case VERY_RARE:
                return level * 50;
            default:
                return super.getMinPower(level);
        }
    }

    @Override
    public int getMaxPower(int level) {
        switch (getRarity()) {
            case UNCOMMON:
                return level * 25;
            case RARE:
                return level * 50;
            case VERY_RARE:
                return level * 100;
            default:
                return super.getMaxPower(level);
        }
    }

    @Override
    public boolean isTreasure() {
        return getRarity() == Rarity.VERY_RARE || getRarity() == Rarity.RARE;
    }

    public Identifier getId() {
        return id;
    }

    public String nbtKey(String key) {
        return id.toString() + key;
    }

    public int level(ItemStack item) {
        return item == null || item.isEmpty() || !item.hasEnchantments() ? 0 : EnchantmentHelper.get(item).getOrDefault(this, 0);
    }

    /**
     * Can regis to any event or other things
     */
    public void register() {
        // Don't replace if exist
        if (Registry.ENCHANTMENT.containsId(id)) {
            return;
        }

        Registry.register(Registry.ENCHANTMENT, id, this);
    }
}
