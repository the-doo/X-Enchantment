package com.doo.xenchantment.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class TrueTrigger extends SimpleCriterionTrigger<TrueTrigger.TriggerInstance> {
    private final ResourceLocation id;

    private TrueTrigger(ResourceLocation id) {
        this.id = id;
    }

    public static TrueTrigger get(ResourceLocation id) {
        return new TrueTrigger(id);
    }

    public void trigger(ServerPlayer serverPlayer) {
        super.trigger(serverPlayer, TriggerInstance::match);
    }

    public ResourceLocation getId() {
        return id;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject jsonObject, Optional<ContextAwarePredicate> optional, DeserializationContext deserializationContext) {
        return new TriggerInstance();
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        public TriggerInstance() {
            super(Optional.empty());
        }

        public boolean match() {
            return true;
        }
    }
}
