package me.miquiis.record.common.managers;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.budschie.bmorph.main.BMorphMod;
import de.budschie.bmorph.morph.MorphManagerHandlers;
import de.budschie.bmorph.morph.MorphUtil;
import me.miquiis.record.Record;
import me.miquiis.record.common.models.*;
import me.miquiis.record.common.utils.MessageUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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

    public void startRecording(ServerPlayerEntity player, String tape, String take, ResourceLocation entity)
    {
        if (isRecording(player.getUniqueID())) return;
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("id", entity.toString());
        //MorphUtil.morphToServer(Optional.of(MorphManagerHandlers.FALLBACK.createMorph(ForgeRegistries.ENTITIES.getValue(entity), nbt, null, true)), Optional.empty(), player);
        recording.put(player.getUniqueID(), new RecordingTake(tape, take, entity.toString()));
    }

    public void pauseRecording(ServerPlayerEntity player)
    {
        if (!isRecording(player.getUniqueID())) return;
        RecordingTake recordingTake = getRecordingTake(player.getUniqueID());
        if(recordingTake.togglePause())
        {
            if (sendFeedback)
            MessageUtils.sendMessage(player, "&eRecording is now paused.");
        }
        else
        {
            if (sendFeedback)
                MessageUtils.sendMessage(player, "&aRecording is now resumed.");
        }
    }

    public void pausePlaying(ServerPlayerEntity player)
    {
        if (currentPlaying.isEmpty()) return;
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        currentPlaying.forEach((s, playTakes) -> {
            playTakes.forEach(playTake -> {
                if (!atomicBoolean.get())
                {
                    if (isSendingFeedback())
                        MessageUtils.sendMessage(player, playTake.togglePause() ? "&cPlaying paused." : "&aPlaying Resumed");
                    atomicBoolean.set(true);
                }
            });
        });
    }

    public void handlePause(ServerPlayerEntity player)
    {
        if (isRecording(player.getUniqueID()))
        {
            pauseRecording(player);
            return;
        }
        else
        {
            pausePlaying(player);
        }
    }

    public void stopRecording(ServerPlayerEntity recorder)
    {
        if (!isRecording(recorder.getUniqueID())) return;

        if (isSendingFeedback())
            MessageUtils.sendMessage(recorder, "&cRecording stopped.");

        RecordingTake recordingTake = getRecordingTake(recorder.getUniqueID());
        recording.remove(recorder.getUniqueID());

        RecordTape recordTape = getRecordTape(recordingTake.recordingTape);
        recordTape.addTake(new RecordTake(recordingTake.recordingTape, recordingTake.recordingTake, recordingTake.recordingEntity, recordingTake.recordScript));

        mod.getPathfindingFolder().saveObject(recordTape.tapeName, recordTape);

        //MorphUtil.morphToServer(Optional.empty(), Optional.empty(), recorder);
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
                nbt.putBoolean("Recording", true);
                try {
                    nbt.merge(JsonToNBT.getTagFromJson("{Tags:[\"record\"]}"));
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }

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

    public List<String> peekTapes() {
        List<String> tapes = new ArrayList<>();
        cachedTapes.forEach(cachedTapes -> tapes.add(cachedTapes.tapeName));

        mod.getPathfindingFolder().peakFiles("json").forEach(fileName -> {
            if (!tapes.contains(fileName)) tapes.add(fileName);
        });

        return tapes;
    }

    public List<String> peekTakes(String tape)
    {
        final RecordTape recordTape = getRecordTape(tape);
        List<String> takes = new ArrayList<>();
        recordTape.takes.forEach(tapeTakes -> takes.add(tapeTakes.takeName));
        return takes;
    }

    public boolean isSendingFeedback() {
        return sendFeedback;
    }

    public boolean isPlaying(String uuid)
    {
        return currentPlaying.containsKey(uuid);
    }

}
