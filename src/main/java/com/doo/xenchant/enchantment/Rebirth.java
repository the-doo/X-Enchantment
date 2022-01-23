package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;

/**
 * 重生
 */
public class Rebirth extends BaseEnchantment {

    public static final String NAME = "rebirth";

    public Rebirth() {
        super(new Identifier(Enchant.ID, NAME), Rarity.UNCOMMON, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMinPower(int level) {
        return level * 25;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 50;
    }

    @Override
    public void register() {
        super.register();

        ServerPlayerEvents.ALLOW_DEATH.register(((player, damageSource, damageAmount) -> EnchantUtil.rebirth(player)));
    }
}
