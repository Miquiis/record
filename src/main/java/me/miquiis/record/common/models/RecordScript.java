package me.miquiis.record.common.models;

import java.util.ArrayList;
import java.util.List;

public class RecordScript {

    public String name;
    public List<RecordTick> ticks;

    public RecordScript(String name)
    {
        this.name = name;
        this.ticks = new ArrayList<>();
    }

    public void addTick(RecordTick tick)
    {
        this.ticks.add(tick);
    }

    public int getMaxTicks()
    {
        return ticks.size();
    }

    public RecordTick getFirstTick()
    {
        if (getMaxTicks() == 0) return null;
        return this.ticks.get(0);
    }

    public RecordTick getLastTick()
    {
        if (getMaxTicks() == 0) return null;
        return this.ticks.get(getMaxTicks() - 1);
    }

}
