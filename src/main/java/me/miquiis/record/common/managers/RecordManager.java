package me.miquiis.record.common.managers;

import me.miquiis.record.Record;
import me.miquiis.record.common.models.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import java.util.*;

public class RecordManager {

    private Record mod;

    private Map<UUID, List<RecordTake>> currentPlaying;
    private Map<UUID, RecordingTake> recording;
    private Set<RecordTape> cachedTapes;

    public RecordManager(Record mod) {
        this.mod = mod;
        this.currentPlaying = new HashMap<>();
        this.recording = new HashMap<>();
        this.cachedTapes = new HashSet<>();
    }

    public void startRecording(UUID recorder, String tape, String take)
    {
        if (isRecording(recorder)) return;
        recording.put(recorder, new RecordingTake(tape, take));
    }

    public void stopRecording(UUID recorder)
    {
        if (!isRecording(recorder)) return;

        RecordingTake recordingTake = getRecordingTake(recorder);
        recording.remove(recorder);

        RecordTape recordTape = getRecordTape(recordingTake.recordingTape);
        recordTape.addTake(new RecordTake(recordingTake.recordingTake, ));

        cachedTapes.add(recordTape);

        mod.getPathfindingFolder().saveObject(recordScript.name, recordScript);
    }

    public void stopPlaying(UUID uuid)
    {
        if (!isPlaying(uuid)) return;
        currentPlaying.remove(uuid);
    }

    public int startPlaying(ServerPlayerEntity player, String name, EntityType<?> entityType, CompoundNBT nbt)
    {
        final RecordManager recordManager = Record.getInstance().getRecordManager();
        final RecordScript recordScript = recordManager.getRecordScript(name);

        if (recordScript == null)
        {
            player.sendMessage(new StringTextComponent("The recording named " + name + " was not found."), null);
            return -1;
        }

        if (recordScript.getFirstTick() == null)
        {
            player.sendMessage(new StringTextComponent("The recording named " + name + " has no animation."), null);
            return -1;
        }

        if (entityType == null)
        {
            entityType = EntityType.PLAYER;
        }

        Entity spawnedEntity = entityType.spawn(player.getServerWorld(), null, null, new BlockPos(recordScript.getFirstTick().posx, recordScript.getFirstTick().posy, recordScript.getFirstTick().posz), SpawnReason.COMMAND, false,  false);

        spawnedEntity.read(spawnedEntity.serializeNBT().merge(nbt));

        currentPlaying.put(spawnedEntity.getUniqueID(), new PlayScript(recordScript));

        player.sendMessage(new StringTextComponent("Animation is starting now."), null);

        return 1;
    }

    public RecordingTake getRecordingTake(UUID uuid)
    {
        if (!isRecording(uuid)) return null;
        return recording.get(uuid);
    }


    public RecordTape getRecordTape(String name)
    {
        RecordTape recordTape = cachedTapes.stream().filter(cachedScripts -> cachedScripts.tapeName.equals(name)).findFirst().orElse(null);

        if (recordTape == null)
        {
            recordTape = mod.getPathfindingFolder().loadObject(name, RecordTape.class, false);
            if (recordTape != null)
            {
                cachedTapes.add(recordTape);
            } else
            {
                recordTape = new RecordTape(name);
            }
        }

        return recordTape;
    }

    public boolean isRecording(UUID uuid)
    {
        return recording.containsKey(uuid);
    }

    public boolean isPlaying(UUID uuid)
    {
        return currentPlaying.containsKey(uuid);
    }

}
