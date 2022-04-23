package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.attribute.LimitTimeModifier;
import com.doo.xenchant.events.LivingApi;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Attribute Halo - enchantment version of attribute
 */
public class AttrHalo extends LivingHalo {

    public static final String NAME = "attribute";

    public static boolean regis = false;

    private static final List<Attribute> ATTRIBUTES = new ArrayList<>();

    private final Attribute attribute;

    public AttrHalo(Attribute attribute) {
        super(NAME + "_-_" + attribute.getDescriptionId());

        this.attribute = attribute;

        ATTRIBUTES.add(attribute);
    }

    @Override
    public String getDescriptionId() {
        return "enchantment.x_enchant.halo_attribute";
    }

    @Override
    public Component getFullname(int level) {
        TranslatableComponent mutableText = new TranslatableComponent(getDescriptionId(), new TranslatableComponent(attribute.getDescriptionId()).getString());
        mutableText.withStyle(ChatFormatting.GOLD);

        if (level != 1 || this.getMaxLevel() != 1) {
            mutableText.append(" ").append(new TranslatableComponent("enchantment.level." + level));
        }

        return mutableText;
    }

    @Override
    public int getMaxLevel() {
        return attribute == null || attribute.getDefaultValue() > 1 ? 5 : 1;
    }

    @Override
    public boolean isTradeable() {
        return true;
    }

    @Override
    public void register() {
        super.register();

        if (regis) {
            return;
        }
        regis = true;

        LivingApi.SEVER_TAIL_TICK.register(living -> {
            AttributeMap attributes = living.getAttributes();
            ATTRIBUTES.forEach(a -> {
                AttributeInstance instance = attributes.getInstance(a);
                if (instance != null) {
                    instance.getModifiers().forEach(m -> {
                        if ((m instanceof LimitTimeModifier && ((LimitTimeModifier) m).isExpire())) {
                            instance.removeModifier(m);
                        }
                    });
                }
            });
        });
    }

    @Override
    public void onTarget(LivingEntity entity, Integer level, List<LivingEntity> targets) {
        targets.forEach(e -> {
            AttributeInstance attr = e.getAttributes().getInstance(attribute);
            if (attr == null) {
                return;
            }

            double value = level + (attr.getBaseValue() == 0 ? 1 : attr.getBaseValue());
            AttributeModifier.Operation op = attr.getBaseValue() == 0 ?
                    AttributeModifier.Operation.MULTIPLY_TOTAL : AttributeModifier.Operation.ADDITION;

            addOrResetModifier(attr, LimitTimeModifier.get(getId().toString(), value, op, (int) (e.tickCount + SECOND * 1.2), e));
        });
    }

    /**
     * 添加或更新修改值
     *
     * @param attr     修改的属性
     * @param modifier 修改值 默认值
     */
    private void addOrResetModifier(AttributeInstance attr, LimitTimeModifier modifier) {
        Optional<AttributeModifier> optional = attr.getModifiers().stream()
                .filter(m -> m.getName().equals(getId().toString()) && m instanceof LimitTimeModifier).findAny();

        if (optional.isPresent()) {
            ((LimitTimeModifier) optional.get()).reset((float) (SECOND * 1.2));
        } else {
            attr.addTransientModifier(modifier);
        }
    }
}
