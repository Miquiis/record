package me.miquiis.record.common.models;

public class RecordTake {

    public String tapeName;
    public String takeName;
    public String takeEntity;
    public RecordScript takeScript;

    public RecordTake(String tapeName, String takeName, String takeEntity, RecordScript takeScript)
    {
        this.tapeName = tapeName;
        this.takeName = takeName;
        this.takeEntity = takeEntity;
        this.takeScript = takeScript;
    }

}
