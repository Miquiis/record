package me.miquiis.record.common.models;

import com.google.gson.annotations.SerializedName;
import net.minecraft.entity.Entity;

public class RecordTickEvent {

    @SerializedName("type")
    private String typeName;

    public RecordTickEvent()
    {
        typeName = getClass().getName();
    }

    public void execute(Entity entity)
    {

    }

}