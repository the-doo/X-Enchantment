package com.doo.xenchant.enchantment.halo;

import com.doo.xenchant.attribute.LimitTimeModifier;
import com.doo.xenchant.mixin.interfaces.ServerLivingApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Attribute Halo - enchantment version of attribute
 */
public class AttrHalo extends LivingHalo {

    public static final String NAME = "attribute";

    private static final List<EntityAttribute> ATTRIBUTES = new ArrayList<>();

    private final EntityAttribute attribute;

    public AttrHalo(EntityAttribute attribute) {
        super(NAME + "_-_" + attribute.getTranslationKey());

        this.attribute = attribute;

        ATTRIBUTES.add(attribute);
    }

    @Override
    public String getTranslationKey() {
        return "enchantment.x_enchant.halo_attribute";
    }

    @Override
    public Text getName(int level) {
        TranslatableText mutableText = new TranslatableText(getTranslationKey(), new TranslatableText(attribute.getTranslationKey()).getString());
        mutableText.formatted(Formatting.GOLD);

        if (level != 1 || this.getMaxLevel() != 1) {
            mutableText.append(" ").append(new TranslatableText("enchantment.level." + level));
        }

        return mutableText;
    }

    @Override
    public int getMaxLevel() {
        return attribute == null || attribute.getDefaultValue() > 1 ? 5 : 1;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public void register() {
        super.register();

        ServerLivingApi.TAIL_TICK.register(living -> {
            AttributeContainer attributes = living.getAttributes();
            ATTRIBUTES.forEach(a -> {
                EntityAttributeInstance instance = attributes.getCustomInstance(a);
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
            EntityAttributeInstance attr = e.getAttributes().getCustomInstance(attribute);
            if (attr == null) {
                return;
            }

            double value = level + (attr.getBaseValue() == 0 ? 1 : attr.getBaseValue());
            EntityAttributeModifier.Operation op = attr.getBaseValue() == 0 ?
                    EntityAttributeModifier.Operation.MULTIPLY_TOTAL : EntityAttributeModifier.Operation.ADDITION;

            addOrResetModifier(attr, LimitTimeModifier.get(getId().toString(), value, op, (int) (e.age + SECOND * 1.2), e));
        });
    }

    /**
     * 添加或更新修改值
     *
     * @param attr     修改的属性
     * @param modifier 修改值 默认值
     */
    private void addOrResetModifier(EntityAttributeInstance attr, LimitTimeModifier modifier) {
        Optional<EntityAttributeModifier> optional = attr.getModifiers().stream()
                .filter(m -> m.getName().equals(getId().toString()) && m instanceof LimitTimeModifier).findAny();

        if (optional.isPresent()) {
            ((LimitTimeModifier) optional.get()).reset((float) (SECOND * 1.2));
        } else {
            attr.addTemporaryModifier(modifier);
        }
    }
}
