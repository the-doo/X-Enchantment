package com.doo.xenchant.enchantment;

import com.doo.xenchant.Enchant;
import com.doo.xenchant.util.EnchantUtil;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

/**
 * Increment Attack Damage
 */
public class IncDamage extends BaseEnchantment {

    public static final String NAME = "increment_attack_damage";

    public IncDamage() {
        super(new Identifier(Enchant.ID, NAME), Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 5 + (level - 1) * 25;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof ToolItem;
    }

    private String nptKey() {
        return getId().toString() + "Damages";
    }

    /**
     * Default DIAMOND is 1 base
     *
     * @param durability tool durability
     * @return inc value
     */
    private float inc(int durability) {
        return 1F * durability / ToolMaterials.DIAMOND.getDurability();
    }

    @Override
    public void register() {
        super.register();

        // inc point when killed other
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(((world, entity, killedEntity) -> {
            if (!(entity instanceof LivingEntity)) {
                return;
            }

            // check level
            ItemStack stack = EnchantUtil.getHandStack((LivingEntity) entity, ToolItem.class);
            if (stack.isEmpty()) {
                return;
            }

            NbtCompound compound = stack.getOrCreateNbt();
            ToolItem item = (ToolItem) stack.getItem();

            float now = compound.getFloat(nptKey());
            float max = item.getMaterial().getAttackDamage() * level(stack);
            if (now >= max) {
                return;
            }

            // inc = random scale * inc()
            float inc = ((LivingEntity) entity).getRandom().nextFloat() * inc(item.getMaterial().getDurability());
            if (inc > 0) {
                compound.putFloat(nptKey(), Math.min(max, now + inc));
            }
        }));
    }

    @Override
    public float getAttackDamage(ItemStack item, int level, EntityGroup group) {
        return item.getOrCreateNbt().getFloat(nptKey());
    }
}
