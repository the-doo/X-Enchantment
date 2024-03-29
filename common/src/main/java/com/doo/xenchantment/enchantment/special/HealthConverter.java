package com.doo.xenchantment.enchantment.special;

import com.doo.xenchantment.interfaces.OneLevelMark;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class HealthConverter extends Special implements OneLevelMark {

    public static final String CONSUMER_KEY = "consumer";

    public static final String HURT_KEY = "hurt";

    public static final String BAN_KEY = "ban";

    private static final List<Item> BAN_LIST = Lists.newArrayList();

    public HealthConverter() {
        super("health_converter", EnchantmentCategory.BREAKABLE, EquipmentSlot.values());

        options.addProperty(CONSUMER_KEY, false);
        options.addProperty(HURT_KEY, 10);
        options.add(BAN_KEY, new JsonArray());
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, CONSUMER_KEY);
        loadIf(json, HURT_KEY);
        loadIf(json, BAN_KEY);

        BAN_LIST.clear();
        foreach(BAN_KEY, e -> BuiltInRegistries.ITEM.getOptional(new ResourceLocation(e.getAsString()))
                .ifPresent(BAN_LIST::add));
    }

    @Override
    public void onOptionsRegister(BiConsumer<String, Supplier<Stream<String>>> register) {
        super.onOptionsRegister(register);

        register.accept(BAN_KEY, () -> BuiltInRegistries.ITEM.stream()
                .filter(i -> i.getDefaultInstance().isDamageableItem())
                .map(Item::getDescriptionId));
    }

    @Override
    public boolean canEnchant(@NotNull ItemStack itemStack) {
        return false;
    }

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment enchantment) {
        return false;
    }

    @Override
    public void onEndTick(LivingEntity living) {
        if (!(living instanceof ServerPlayer player) || living.tickCount % (10 * SECOND_TICK) != 0) {
            return;
        }

        int level = level(living.getOffhandItem());
        if (level < 1) {
            return;
        }

        ItemStack item = living.getMainHandItem();
        if (item.isEmpty() || BAN_LIST.contains(item.getItem()) || !item.isDamageableItem() || item.getDamageValue() < 1) {
            return;
        }

        float damage = (float) doubleV(HURT_KEY);
        item.setDamageValue(0);
        living.hurt(player.damageSources().magic(), damage);

        player.playSound(SoundEvents.ANVIL_USE);

        if (boolV(CONSUMER_KEY) && !player.isCreative()) {
            ListTag tag = EnchantedBookItem.getEnchantments(living.getOffhandItem());
            resetLevel(level, living.getOffhandItem(), tag);
        }
    }
}
