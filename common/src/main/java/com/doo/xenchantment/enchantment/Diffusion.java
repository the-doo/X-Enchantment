package com.doo.xenchantment.enchantment;

import com.doo.xenchantment.events.ArrowApi;
import com.doo.xenchantment.interfaces.ArrowAccessor;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

/**
 * Diffusion
 */
public class Diffusion extends BaseXEnchantment {

    private static final String MONSTER_KEY = "monster";
    private static final String DAMAGE_KEY = "damage";
    private static final String RANGE_KEY = "range";
    private static final String COUNT_KEY = "count";
    private static final ArrowItem ARROW_ITEM = (ArrowItem) Items.ARROW.asItem();

    public Diffusion() {
        super("diffusion", Rarity.RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});

        options.addProperty(MAX_LEVEL_KEY, 3);
        options.addProperty(MONSTER_KEY, true);
        options.addProperty(DAMAGE_KEY, 15);
        options.addProperty(RANGE_KEY, 5);
        options.addProperty(COUNT_KEY, 1);
    }

    @Override
    public void loadOptions(JsonObject json) {
        super.loadOptions(json);

        loadIf(json, MONSTER_KEY);
        loadIf(json, DAMAGE_KEY);
        loadIf(json, RANGE_KEY);
        loadIf(json, COUNT_KEY);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ProjectileWeaponItem;
    }

    @Override
    public void onServer() {
        ArrowApi.register((arrow, attacker, itemStack, entity, damage) -> {
            if (disabled() || !ArrowAccessor.get(arrow).canDiffusion()) {
                return;
            }

            int level = level(itemStack);
            if (level < 1) {
                return;
            }

            Level world = attacker.level();
            float amount = (float) (damage * level * (getDouble(DAMAGE_KEY) / 100));
            long count = (long) (level * getDouble(COUNT_KEY));
            Predicate<LivingEntity> test = e -> e != entity && e != attacker && !e.isAlliedTo(attacker) && (!getBoolean(MONSTER_KEY) || e instanceof Monster);
            world.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate((int) getDouble(RANGE_KEY)), test)
                    .stream().limit(count)
                    .forEach(e -> initArrow(world, amount, attacker, entity, e));
        });
    }

    private void initArrow(Level world, float damage, LivingEntity user, LivingEntity source, LivingEntity e) {
        AbstractArrow arrow = ARROW_ITEM.createArrow(world, ItemStack.EMPTY, user);
        arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
        arrow.setBaseDamage(damage);
        Vec3 eP = e.getPosition(0);
        arrow.setPos(eP.x, eP.y + e.getEyeHeight() + 1, eP.z);
        arrow.setXRot(0);
        arrow.setYRot(1);
        ArrowAccessor.get(arrow).disableDiffusion();
        world.addFreshEntity(arrow);
    }
}
