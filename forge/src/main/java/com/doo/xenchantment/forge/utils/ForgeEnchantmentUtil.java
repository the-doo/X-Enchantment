package com.doo.xenchantment.forge.utils;

import com.doo.playerinfo.XPlayerInfo;
import com.doo.xenchantment.enchantment.BrokenDawn;
import com.doo.xenchantment.enchantment.IncDamage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraftforge.fml.ModList;

import java.util.Optional;

public class ForgeEnchantmentUtil {

    private ForgeEnchantmentUtil() {
    }

    public static void init() {
        if (ModList.get().isLoaded("silentgear")) {
            loadSilentGear();
        }
    }

    private static void loadSilentGear() {
        BrokenDawn.ITEM_SWITCHERS.add(stack -> {
            CompoundTag tag = stack.getTag();
            if (!stack.hasTag() || !tag.contains("SGear_Data")) {
                return null;
            }

            Item item = stack.getItem();
            float damage = sgMaxDamage(tag);
            if (item instanceof ArmorItem ia) {
                float value = sgMaxArmor(tag);
                return i -> i instanceof ArmorItem ai && ia.getEquipmentSlot() == ai.getEquipmentSlot() &&
                        ai.getMaxDamage() > damage && ai.getDefense() > value;
            }
            if (item instanceof SwordItem) {
                float value = sgAttack(tag);
                return i -> i instanceof SwordItem si && si.getDamage() > value && si.getMaxDamage() > damage;
            }
            if (item instanceof TieredItem) {
                float level = sgMaxLevel(tag);
                float uses = sgMaxUse(tag);
                return i -> i instanceof TieredItem ti && ti.getMaxDamage() > damage &&
                        (ti.getTier().getLevel() > level || ti.getTier().getSpeed() > uses);
            }

            return null;
        });

        IncDamage.DAMAGE_GETTER.add(stack -> {
            if (!stack.hasTag() || !isSGSwordItem(stack.getTag())) {
                return null;
            }

            return sgAttack(stack.getTag());
        });
    }

    private static boolean isSGSwordItem(CompoundTag tag) {
        return XPlayerInfo.isForge() && tag.contains("SGear_Data") &&
                Optional.ofNullable(tag.getCompound("SGear_Data")).map(c ->
                        Optional.ofNullable(c.getCompound("Properties")).map(c1 ->
                                Optional.ofNullable(c1.getCompound("Stats"))
                                        .map(c2 -> c2.contains("silentgear:melee_damage"))
                                        .orElse(false)).orElse(false)).orElse(false);
    }

    private static float sgAttack(CompoundTag tag) {
        return tag.getCompound("SGear_Data").getCompound("Properties")
                .getCompound("Stats").getFloat("silentgear:melee_damage");
    }

    private static float sgMaxDamage(CompoundTag tag) {
        return tag.getCompound("SGear_Data").getCompound("Properties")
                .getCompound("Stats").getFloat("silentgear:durability");
    }

    private static float sgMaxArmor(CompoundTag tag) {
        return tag.getCompound("SGear_Data").getCompound("Properties")
                .getCompound("Stats").getFloat("silentgear:armor");
    }

    private static float sgMaxLevel(CompoundTag tag) {
        return tag.getCompound("SGear_Data").getCompound("Properties")
                .getCompound("Stats").getFloat("silentgear:harvest_level");
    }

    private static float sgMaxUse(CompoundTag tag) {
        return tag.getCompound("SGear_Data").getCompound("Properties")
                .getCompound("Stats").getFloat("silentgear:harvest_speed");
    }

}
