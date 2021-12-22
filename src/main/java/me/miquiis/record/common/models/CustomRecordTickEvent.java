package me.miquiis.record.common.models;

import me.miquiis.record.common.events.custom.RecordEventPlayEvent;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;

public class CustomRecordTickEvent extends RecordTickEvent {

    private String eventLabel;
    private String eventValue;

    public CustomRecordTickEvent(String eventLabel, String eventValue) {
        this.eventLabel = eventLabel;
        this.eventValue = eventValue;
    }

    @Override
    public void execute(Entity entity) {
        super.execute(entity);
        MinecraftForge.EVENT_BUS.post(new RecordEventPlayEvent(entity, eventLabel, eventValue));
    }

    public String getEventLabel() {
        return eventLabel;
    }

    public String getEventValue() {
        return eventValue;
    }
}