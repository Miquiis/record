package me.miquiis.record.common.events.custom;

import net.minecraft.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

@Cancelable
public class RecordEventChatEvent extends Event {

    private final Entity entity;
    private final String playerName;
    private final String message;

    public RecordEventChatEvent(Entity entity, String playerName, String message) {
        this.entity = entity;
        this.playerName = playerName;
        this.message = message;
    }

    public Entity getEntity() {
        return entity;
    }
}
