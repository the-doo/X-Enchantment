package com.doo.xenchantment.interfaces;

import com.doo.xenchantment.enchantment.BaseXEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public interface WithAttribute<T extends BaseXEnchantment> {

    List<UUID[]> getUUIDs();

    List<Attribute> getAttribute();

    AttributeModifier getMatchModify(Attribute attribute, ItemStack stack, int level);

    @SuppressWarnings("unchecked")
    default T get() {
        return (T) this;
    }

    default void insertAttr(ItemStack stack, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> modifier) {
        T t = get();
        if (t.disabled() || stack.getItem() instanceof EnchantedBookItem) {
            return;
        }
        if ((stack.getItem() instanceof Equipable e) && slot != e.getEquipmentSlot()) {
            return;
        }
        if (Arrays.stream(t.slots).noneMatch(s -> s == slot)) {
            return;
        }

        int level = t.level(stack);
        if (level < 1) {
            return;
        }

        modifiedAttrMap(stack, level, modifier);
    }

    default void modifiedAttrMap(ItemStack stack, int level, BiConsumer<Attribute, AttributeModifier> modifier) {
        getAttribute().forEach(a -> modifier.accept(a, getMatchModify(a, stack, level)));
    }

    default Iterable<ItemStack> effectStack(Player player) {
        return get().getSlotItems(player).values();
    }

    default int stackIdx(ItemStack stack, EquipmentSlot[] slots) {
        if (!(stack.getItem() instanceof Equipable e)) {
            return 0;
        }
        int idx = 0;
        for (EquipmentSlot slot : slots) {
            if (slot == e.getEquipmentSlot()) {
                return idx;
            }
            idx++;
        }
        return 0;
    }

    default AttributeModifier oneAttrModify(int index, int level, double value, AttributeModifier.Operation op) {
        return new AttributeModifier(getUUIDs().get(0)[index], get().name(), value / 100 * level, op);
    }

    default AttributeModifier[] allModifies(Attribute attribute, ItemStack stack, int level) {
        return new AttributeModifier[]{getMatchModify(attribute, stack, level)};
    }

    default void reloadAttr(Player player) {
        T t = get();
        AttributeMap map = player.getAttributes();
        List<Attribute> attribute = getAttribute();
        if (t.disabled()) {
            List<UUID[]> uuids = getUUIDs();
            for (int i = 0; i < attribute.size(); i++) {
                WithAttribute.remove(map, attribute.get(i), uuids.get(i));
            }
            return;
        }

        int level;
        for (ItemStack stack : effectStack(player)) {
            level = t.level(stack);
            if (level < 1) {
                continue;
            }

            for (Attribute value : attribute) {
                WithAttribute.add(map, value, allModifies(value, stack, level));
            }
        }
    }


    static void remove(AttributeMap map, Attribute attribute, UUID... uuids) {
        if (map.hasAttribute(attribute)) {
            AttributeInstance instance = map.getInstance(attribute);
            for (UUID uuid : uuids) {
                instance.removeModifier(uuid);
            }
        }
    }

    static void add(AttributeMap map, Attribute attribute, AttributeModifier... modifiers) {
        AttributeInstance instance = map.getInstance(attribute);
        for (AttributeModifier modifier : modifiers) {
            instance.removeModifier(modifier.getId());
            instance.addTransientModifier(modifier);
        }
    }
}
