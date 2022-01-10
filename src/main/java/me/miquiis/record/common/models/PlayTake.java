package me.miquiis.record.common.models;

import java.util.UUID;

public class PlayTake {

    public String tapeName;
    public String takeName;
    public UUID entityId;
    public PlayScript takeScript;
    public boolean shouldKill;
    public boolean isPaused;

    public PlayTake(String tapeName, String takeName, boolean shouldKill, PlayScript takeScript)
    {
        this.tapeName = tapeName;
        this.takeName = takeName;
        this.shouldKill = shouldKill;
        this.takeScript = takeScript;
        this.isPaused = false;
    }

    public PlayTake(RecordTake recordTake)
    {
        this.tapeName = recordTake.tapeName;
        this.takeName = recordTake.takeName;
        this.shouldKill = false;
        this.takeScript = new PlayScript(recordTake.takeScript);
        this.isPaused = false;
    }

    public PlayTake(RecordTake recordTake, boolean shouldKill, UUID entityId)
    {
        this.tapeName = recordTake.tapeName;
        this.takeName = recordTake.takeName;
        this.takeScript = new PlayScript(recordTake.takeScript);
        this.shouldKill = shouldKill;
        this.entityId = entityId;
        this.isPaused = false;
    }

    public boolean togglePause()
    {
        return this.isPaused = !isPaused;
    }

    public void setEntityId(UUID entityId)
    {
        this.entityId = entityId;
    }

}
