package me.miquiis.record.common.models;

public class PlayScript {

    public RecordScript recordScript;
    public int currentTick;

    public PlayScript(RecordScript recordScript)
    {
        this.recordScript = recordScript;
        this.currentTick = 0;
    }

    public RecordScript.RecordTick playTick()
    {
        if (currentTick == recordScript.getMaxTicks()) return null;
        return recordScript.ticks.get(currentTick++);
    }
}
