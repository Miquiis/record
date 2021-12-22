package me.miquiis.record.common.events.custom;

import net.minecraft.entity.Entity;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

public class RecordEventPlayEvent extends Event {

    private final Entity entity;
    private final String eventLabel;
    private final String eventValue;

    public RecordEventPlayEvent(Entity entity, String eventLabel, @Nullable String eventValue) {
        this.entity = entity;
        this.eventLabel = eventLabel;
        this.eventValue = eventValue;
    }

    public Entity getEntity() {
        return entity;
    }

    public String getEventLabel() {
        return eventLabel;
    }

    public String getEventValue() {
        return eventValue;
    }
}
