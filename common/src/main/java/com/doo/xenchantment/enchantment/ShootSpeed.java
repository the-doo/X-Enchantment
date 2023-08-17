package com.doo.xenchantment.enchantment;

import com.doo.playerinfo.core.InfoGroupItems;
import com.doo.xenchantment.util.EnchantUtil;
import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ShootSpeed extends BaseXEnchantment {

    private static final String VALUE_KEY = "value";

    public ShootSpeed() {
        super("shoot_speed", Rarity.VERY_RARE, EnchantmentCategory.BOW, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);

        options.addProperty(MAX_LEVEL_KEY, 3);
        options.addProperty(VALUE_KEY, 10);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, VALUE_KEY);
    }

    @Override
    public InfoGroupItems collectPlayerInfo(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        int level = level(stack);
        InfoGroupItems group = InfoGroupItems.groupKey(getDescriptionId());
        group.add(getInfoKey(VALUE_KEY), level < 1 ? 0 : doubleV(VALUE_KEY) / 10, true);
        return group;
    }

    public static float speed(float speed, LivingEntity entity, ItemStack stack) {
        if (entity == null || stack == null || stack.isEmpty()) {
            return speed;
        }

        BaseXEnchantment shoot = EnchantUtil.ENCHANTMENTS_MAP.get(ShootSpeed.class);
        int level = shoot.level(stack);
        if (level < 1) {
            return speed;
        }

        return (float) (speed * (1 + shoot.doubleV(VALUE_KEY) / 10));
    }
}
