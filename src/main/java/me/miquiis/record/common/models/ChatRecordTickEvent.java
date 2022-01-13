package me.miquiis.record.common.models;

import me.miquiis.record.common.events.custom.RecordEventChatEvent;
import me.miquiis.record.common.events.custom.RecordEventPlayEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;

import java.util.UUID;

public class ChatRecordTickEvent extends RecordTickEvent {

    private String playerName;
    private String message;

    public ChatRecordTickEvent(String playerName, String message) {
        this.playerName = playerName;
        this.message = message;
    }

    @Override
    public void execute(Entity entity) {
        super.execute(entity);
        if (!MinecraftForge.EVENT_BUS.post(new RecordEventChatEvent(entity, playerName, message)))
        {
            if (entity.world.isRemote) return;
            entity.world.getPlayers().forEach(playerEntity -> {
                playerEntity.sendMessage(new StringTextComponent("<" + playerName + "> " + message), new UUID(0, 0));
            });
        }
    }

    public String getMessage() {
        return message;
    }
}