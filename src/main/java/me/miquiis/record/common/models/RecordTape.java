package me.miquiis.record.common.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordTape {

    public String tapeName;
    public List<RecordTake> takes;

    public RecordTape(String tapeName, RecordTake recordTake)
    {
        this.tapeName = tapeName;
        this.takes = new ArrayList<RecordTake>(){{
            add(recordTake);
        }};
    }

    public RecordTape(String tapeName, List<RecordTake> takes)
    {
        this.tapeName = tapeName;
        this.takes = takes;
    }

    public RecordTape(String tapeName)
    {
        this.tapeName = tapeName;
        this.takes = new ArrayList<>();
    }

    public void addTake(RecordTake recordTake)
    {
        this.takes.removeIf(savedTake -> savedTake.takeName.equalsIgnoreCase(recordTake.takeName));
        this.takes.add(recordTake);
    }

    public RecordTake getTake(String takeName)
    {
        return this.takes.stream().filter(recordTake -> recordTake.takeName.equalsIgnoreCase(takeName)).findFirst().orElse(null);
    }

}
