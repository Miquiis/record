package me.miquiis.record.common.models;

import java.util.UUID;

public class PlayTake {

    public String tapeName;
    public String takeName;
    public UUID entityId;
    public PlayScript takeScript;
    public boolean shouldKill;

    public PlayTake(String tapeName, String takeName, boolean shouldKill, PlayScript takeScript)
    {
        this.tapeName = tapeName;
        this.takeName = takeName;
        this.shouldKill = shouldKill;
        this.takeScript = takeScript;
    }

    public PlayTake(RecordTake recordTake)
    {
        this.tapeName = recordTake.tapeName;
        this.takeName = recordTake.takeName;
        this.shouldKill = false;
        this.takeScript = new PlayScript(recordTake.takeScript);
    }

    public PlayTake(RecordTake recordTake, boolean shouldKill, UUID entityId)
    {
        this.tapeName = recordTake.tapeName;
        this.takeName = recordTake.takeName;
        this.takeScript = new PlayScript(recordTake.takeScript);
        this.shouldKill = shouldKill;
        this.entityId = entityId;
    }

    public void setEntityId(UUID entityId)
    {
        this.entityId = entityId;
    }

}
