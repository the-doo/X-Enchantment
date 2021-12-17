package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.events.S2CFishCaughtSoundCallback;
import com.doo.xenchant.util.EnchantUtil;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.impl.item.ItemExtensions;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

/**
 * 自动钓鱼
 */
public class AutoFish extends BaseEnchantment {

    public static final String NAME = "auto_fish";

    public AutoFish() {
        super(new Identifier(Enchant.ID, NAME),
                Enchantment.Rarity.COMMON,
                EnchantmentTarget.FISHING_ROD,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMinPower(int level) {
        return 20;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public void register() {
        // listener
        S2CFishCaughtSoundCallback.EVENT.register(getId(), (EnchantUtil::autoFish));
//        //

    }
}
