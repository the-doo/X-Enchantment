package com.doo.xenchantment.enchantment.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class TrueTrigger extends SimpleCriterionTrigger<TrueTrigger.TriggerInstance> {
    private final ResourceLocation id;

    private TrueTrigger(ResourceLocation id) {
        this.id = id;
    }

    public static TrueTrigger get(ResourceLocation id) {
        return new TrueTrigger(id);
    }

    @Override
    protected @NotNull TriggerInstance createInstance(JsonObject jsonObject, ContextAwarePredicate contextAwarePredicate, DeserializationContext deserializationContext) {
        return new TriggerInstance(id, ContextAwarePredicate.ANY);
    }

    public void trigger(ServerPlayer serverPlayer) {
        super.trigger(serverPlayer, TriggerInstance::match);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        public TriggerInstance(ResourceLocation resourceLocation, ContextAwarePredicate contextAwarePredicate) {
            super(resourceLocation, contextAwarePredicate);
        }

        public boolean match() {
            return true;
        }
    }
}
