package me.miquiis.record.common.events.custom;

import net.minecraft.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

public class RecordTapeStartEvent extends Event {

    private final String tapeName;
    private final List<String> tapeTakes;

    public RecordTapeStartEvent(String tapeName, List<String> tapeTakes) {
        this.tapeName = tapeName;
        this.tapeTakes = tapeTakes;
    }

    public List<String> getTapeTakes() {
        return tapeTakes;
    }

    public String getTapeName() {
        return tapeName;
    }
}
