package me.miquiis.record.common.events.custom;

import me.miquiis.record.common.models.PlayTake;
import net.minecraftforge.eventbus.api.Event;

public class RecordTapeEndEvent extends Event {

    private final String tapeName;
    private final PlayTake lastTake;

    public RecordTapeEndEvent(String tapeName, PlayTake lastTake)
    {
        this.tapeName = tapeName;
        this.lastTake = lastTake;
    }

    public PlayTake getLastTake() {
        return lastTake;
    }

    public String getTapeName() {
        return tapeName;
    }
}
