package com.doo.xenchant.enchantment;

import com.doo.xenchant.util.EnchantUtil;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Increment Attack Damage
 */
public class IncDamage extends BaseEnchantment {

    public static final String NAME = "increment_attack_damage";

    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    public IncDamage() {
        super(NAME, Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 5 + (level - 1) * 25;
    }

    @Override
    public int getMaxPower(int level) {
        return level * 150;
    }

    @Override
    public int getMaxLevel() {
        return 5;
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

        // inc value when killed other
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(((world, entity, killedEntity) -> {
            if (!(entity instanceof LivingEntity)) {
                return;
            }

            // check level
            ItemStack stack = EnchantUtil.getHandStack((LivingEntity) entity, ToolItem.class);
            if (stack.isEmpty()) {
                return;
            }

            ToolItem item = (ToolItem) stack.getItem();

            NbtCompound compound = stack.getOrCreateNbt();
            float now = compound.getFloat(nptKey());
            float max = 0;
            if (item instanceof SwordItem) {
                max = ((SwordItem) item).getAttackDamage();
            } else if (item instanceof MiningToolItem) {
                max = ((MiningToolItem) item).getAttackDamage();
            }
            max *= level(stack);

            if (now >= max) {
                return;
            }

            // inc = random scale * inc()
            float inc = ((LivingEntity) entity).getRandom().nextFloat() * inc(item.getMaterial().getDurability());
            if (inc > 0) {
                compound.putFloat(nptKey(), Math.min(max, now + inc));
            }
        }));

        // tooltips
        ItemTooltipCallback.EVENT.register((ItemStack stack, TooltipContext context, List<Text> lines) -> {
            if (stack.getOrCreateNbt().contains(nptKey())) {
                lines.add(new TranslatableText(getTranslationKey()).append(": ↑").append(FORMAT.format(stack.getOrCreateNbt().getFloat(nptKey()))));
            }
        });
    }

    @Override
    public float getAdditionDamage(LivingEntity attacker, LivingEntity target, ItemStack stack, int level) {
        return stack.getOrCreateNbt().getFloat(nptKey());
    }
}
