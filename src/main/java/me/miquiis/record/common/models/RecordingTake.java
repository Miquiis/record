package me.miquiis.record.common.models;

public class RecordingTake {

    public String recordingTape;
    public String recordingTake;
    public String recordingEntity;

    public RecordScript recordScript;

    public boolean isPaused;

    public RecordingTake(String recordingTape, String recordingTake, String recordingEntity)
    {
        this.recordingTake = recordingTake;
        this.recordingTape = recordingTape;
        this.recordingEntity = recordingEntity;
        this.recordScript = new RecordScript(recordingTake);
        this.isPaused = false;
    }

    public boolean togglePause()
    {
        return isPaused = !isPaused;
    }

}
