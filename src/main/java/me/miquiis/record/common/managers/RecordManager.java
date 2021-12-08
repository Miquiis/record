package me.miquiis.record.common.managers;

import me.miquiis.record.Record;
import me.miquiis.record.common.models.PlayScript;
import me.miquiis.record.common.models.RecordScript;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import java.util.*;

public class RecordManager {

    private Record mod;

    private Map<UUID, PlayScript> currentPlaying;
    private Map<UUID, String> recording;
    private Set<RecordScript> cachedScripts;

    public RecordManager(Record mod) {
        this.mod = mod;
        this.currentPlaying = new HashMap<>();
        this.recording = new HashMap<>();
        this.cachedScripts = new HashSet<>();
    }

    public void startRecording(UUID recorder, String name)
    {
        if (isRecording(recorder)) return;
        RecordScript recordScript = new RecordScript(name);
        cachedScripts.add(recordScript);
        recording.put(recorder, name);
    }

    public void stopRecording(UUID recorder)
    {
        if (!isRecording(recorder)) return;
        RecordScript recordScript = getRecordScript(recorder);
        recording.remove(recorder);
        cachedScripts.remove(recordScript);
        mod.getPathfindingFile().saveObject(recordScript.name, recordScript);
    }

    public void stopPlaying(UUID uuid)
    {
        if (!isPlaying(uuid)) return;
        currentPlaying.remove(uuid);
    }

    public int startPlaying(ServerPlayerEntity player, String name, EntityType<?> entityType)
    {
        final RecordManager recordManager = MinecraftSchool.getInstance().getRecordManager();
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
            entityType = ModEntityTypes.FAKE_PLAYER.get();
        }

        Entity spawnedEntity = entityType.spawn(player.getServerWorld(), null, null, new BlockPos(recordScript.getFirstTick().posx, recordScript.getFirstTick().posy, recordScript.getFirstTick().posz), SpawnReason.COMMAND, false,  false);

        currentPlaying.put(spawnedEntity.getUniqueID(), new PlayScript(recordScript));

        player.sendMessage(new StringTextComponent("Animation is starting now."), null);

        return 1;
    }

    public PlayScript getPlayScript(UUID uuid)
    {
        if (!isPlaying(uuid)) return null;
        return currentPlaying.get(uuid);
    }

    public RecordScript getRecordScript(UUID uuid)
    {
        if (!isRecording(uuid)) return null;
        return getRecordScript(recording.get(uuid));
    }

    public RecordScript getRecordScript(String name)
    {
        RecordScript recordScript = cachedScripts.stream().filter(cachedScripts -> cachedScripts.name.equals(name)).findFirst().orElse(null);

        if (recordScript == null)
        {
            recordScript = mod.getPathfindingFile().loadObject(name, RecordScript.class, false);
            if (recordScript != null)
            {
                cachedScripts.add(recordScript);
            }
        }

        return recordScript;
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
