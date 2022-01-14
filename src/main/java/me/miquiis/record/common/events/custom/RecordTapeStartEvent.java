package me.miquiis.record.common.events.custom;

import net.minecraft.entity.Entity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

public class RecordTapeStartEvent extends Event {

    private final ServerWorld world;
    private final String tapeName;
    private final List<String> tapeTakes;

    public RecordTapeStartEvent(ServerWorld world, String tapeName, List<String> tapeTakes) {
        this.world = world;
        this.tapeName = tapeName;
        this.tapeTakes = tapeTakes;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public List<String> getTapeTakes() {
        return tapeTakes;
    }

    public String getTapeName() {
        return tapeName;
    }
}
