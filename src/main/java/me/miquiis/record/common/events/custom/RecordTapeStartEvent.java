package me.miquiis.record.common.events.custom;

import me.miquiis.record.common.models.PlayTake;
import net.minecraft.entity.Entity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

public class RecordTapeStartEvent extends Event {

    private final ServerWorld world;
    private final String tapeName;
    private final List<PlayTake> tapeTakes;

    public RecordTapeStartEvent(ServerWorld world, String tapeName, List<PlayTake> tapeTakes) {
        this.world = world;
        this.tapeName = tapeName;
        this.tapeTakes = tapeTakes;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public List<PlayTake> getTapeTakes() {
        return tapeTakes;
    }

    public String getTapeName() {
        return tapeName;
    }
}
