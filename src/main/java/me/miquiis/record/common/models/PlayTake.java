package me.miquiis.record.common.models;

import net.minecraft.entity.Entity;

import java.util.UUID;

public class PlayTake {

    public String tapeName;
    public String takeName;
    public Entity currentEntity;
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

    public PlayTake(RecordTake recordTake, boolean shouldKill, Entity entity)
    {
        this.tapeName = recordTake.tapeName;
        this.takeName = recordTake.takeName;
        this.takeScript = new PlayScript(recordTake.takeScript);
        this.shouldKill = shouldKill;
        this.currentEntity = entity;
        this.entityId = entity.getUniqueID();
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
