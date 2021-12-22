package me.miquiis.record.common.managers;

import com.mojang.brigadier.Message;
import me.miquiis.record.Record;
import me.miquiis.record.common.models.*;
import me.miquiis.record.common.utils.MessageUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class RecordManager {

    private Record mod;

    private Map<String, List<PlayTake>> currentPlaying;
    private Map<UUID, RecordingTake> recording;
    private Set<RecordTape> cachedTapes;

    private boolean sendFeedback;

    public RecordManager(Record mod) {
        this.mod = mod;
        this.currentPlaying = new HashMap<>();
        this.recording = new HashMap<>();
        this.cachedTapes = new HashSet<>();
        this.sendFeedback = true;
    }

    public boolean toggleFeedback()
    {
        return sendFeedback = !sendFeedback;
    }

    public void startRecording(UUID recorder, String tape, String take, String entity)
    {
        if (isRecording(recorder)) return;
        recording.put(recorder, new RecordingTake(tape, take, entity));
    }

    public void stopRecording(UUID recorder)
    {
        if (!isRecording(recorder)) return;

        RecordingTake recordingTake = getRecordingTake(recorder);
        recording.remove(recorder);

        RecordTape recordTape = getRecordTape(recordingTake.recordingTape);
        recordTape.addTake(new RecordTake(recordingTake.recordingTape, recordingTake.recordingTake, recordingTake.recordingEntity, recordingTake.recordScript));

        cachedTapes.add(recordTape);

        mod.getPathfindingFolder().saveObject(recordTape.tapeName, recordTape);
    }

    public void stopPlaying(String tape)
    {
        if (!isPlaying(tape)) return;
        currentPlaying.remove(tape);
    }

    public boolean stopPlaying(String tape, String take)
    {
        if (!isPlaying(tape)) return true;
        List<PlayTake> playTakes = currentPlaying.get(tape);
        if (playTakes == null) return true;
        playTakes.removeIf(playTake -> playTake.takeName.equalsIgnoreCase(take));

        if (playTakes.isEmpty())
        {
            currentPlaying.remove(tape);
            return true;
        }

        currentPlaying.put(tape, playTakes);
        return false;
    }

    public int createCustomEvent(ServerPlayerEntity player, String label, String value) {
        final Record instance = Record.getInstance();
        final RecordManager recordManager = instance.getRecordManager();
        if (!recordManager.isRecording((player.getUniqueID()))) {
            MessageUtils.sendMessage(player, "&cYou are not recording anything at the moment");
            return -1;
        }
        recordManager.getRecordingTake(player.getUniqueID()).recordScript.getLastTick().addRecordTickEvent(new CustomRecordTickEvent(
                label, value
        ));

        MessageUtils.sendMessage(player, "&aCustom event has been added.");
        return 1;
    }

    public int startPlaying(ServerPlayerEntity player, String tapeName, boolean shouldKill, @Nonnull List<String> whitelist)
    {
        final RecordManager recordManager = Record.getInstance().getRecordManager();
        final RecordTape recordTape = recordManager.getRecordTape(tapeName);

        if (recordTape == null && isSendingFeedback())
        {
            MessageUtils.sendMessage(player, "&cThe recording named " + tapeName + " was not found.");
            return -1;
        }

        List<RecordTake> recordTakes = recordTape.takes.stream().filter(recordTake -> whitelist.isEmpty() || whitelist.contains(recordTake.takeName)).collect(Collectors.toList());

        if (recordTakes.isEmpty()&& isSendingFeedback())
        {
            MessageUtils.sendMessage(player, "&cThe recording tape named " + tapeName + " has no animations.");
            return -1;
        }

        List<PlayTake> playTakes = new ArrayList<>();

        recordTakes.forEach(recordTake -> {
            final Optional<EntityType<?>> optionalEntityType = EntityType.byKey(recordTake.takeEntity);
            final RecordScript recordScript = recordTake.takeScript;
            optionalEntityType.ifPresent(entityType -> {
                Entity spawnedEntity = entityType.spawn(player.getServerWorld(), null, null, new BlockPos(recordScript.getFirstTick().posx, recordScript.getFirstTick().posy, recordScript.getFirstTick().posz), SpawnReason.COMMAND, false,  false);
                CompoundNBT nbt = spawnedEntity.serializeNBT();
                nbt.putBoolean("NoAI", true);

                spawnedEntity.deserializeNBT(nbt);
                playTakes.add(new PlayTake(recordTake, shouldKill, spawnedEntity.getUniqueID()));
            });
        });

        currentPlaying.put(recordTape.tapeName, playTakes);

        if (isSendingFeedback())
            MessageUtils.sendMessage(player, "&aAnimation is starting now.");

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

    public PlayTake getEntityTake(UUID uuid)
    {
        for (List<PlayTake> playTakes : currentPlaying.values())
        {
            for (PlayTake playTake : playTakes)
            {
                if (playTake.entityId.equals(uuid))
                {
                    return playTake;
                }
            }
        }
        return null;
    }

    public boolean isSendingFeedback() {
        return sendFeedback;
    }

    public boolean isPlaying(String uuid)
    {
        return currentPlaying.containsKey(uuid);
    }

}
