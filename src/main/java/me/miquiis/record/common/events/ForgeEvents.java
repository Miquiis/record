package me.miquiis.record.common.events;

import me.miquiis.record.Record;
import me.miquiis.record.common.events.custom.RecordTapeEndEvent;
import me.miquiis.record.common.managers.RecordManager;
import me.miquiis.record.common.models.*;
import me.miquiis.record.server.commands.RecordCommand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = Record.MOD_ID)
public class ForgeEvents {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event)
    {
        new RecordCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onPlayerTakeDamage(LivingDamageEvent event)
    {
        if (!event.getEntity().world.isRemote)
        {
            final Record instance = Record.getInstance();
            final RecordManager recordManager = instance.getRecordManager();
            if (!recordManager.isRecording((event.getEntity().getUniqueID()))) return;
            recordManager.getRecordingTake(event.getEntity().getUniqueID()).recordScript.getLastTick().addRecordTickEvent(new HitRecordTickEvent(
                    event.getAmount()
            ));
        }
    }

    @SubscribeEvent
    public static void onPlayerSendMessage(ServerChatEvent event)
    {
        if (!event.getPlayer().world.isRemote)
        {
            final Record instance = Record.getInstance();
            final RecordManager recordManager = instance.getRecordManager();
            if (!recordManager.isRecording((event.getPlayer().getUniqueID()))) return;
            recordManager.getRecordingTake(event.getPlayer().getUniqueID()).recordScript.getLastTick().addRecordTickEvent(new ChatRecordTickEvent(
                    event.getUsername(),
                    event.getMessage()
            ));
        }
    }

    @SubscribeEvent
    public static void onPlayerDropItem(ItemTossEvent event)
    {
        if (!event.getPlayer().world.isRemote)
        {
            final Record instance = Record.getInstance();
            final RecordManager recordManager = instance.getRecordManager();
            if (!recordManager.isRecording((event.getPlayer().getUniqueID()))) return;
            recordManager.getRecordingTake(event.getPlayer().getUniqueID()).recordScript.getLastTick().addRecordTickEvent(new ItemRecordTickEvent(
                    event.getEntityItem().getItem().getItem().getRegistryName().toString(),
                    event.getEntityItem().getItem().getCount(),
                    event.getEntityItem().getItem().getOrCreateTag().toString()
            ));
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.PlayerTickEvent event)
    {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END)
        {
            final Record instance = Record.getInstance();
            final RecordManager recordManager = instance.getRecordManager();

            if (!recordManager.isRecording((event.player.getUniqueID()))) return;

            final RecordingTake recordingTake = recordManager.getRecordingTake(event.player.getUniqueID());

            if (recordingTake.isPaused) return;

            recordingTake.recordScript.addTick(new RecordTick(event.player));
        }
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event)
    {
        if (event.getEntity().getEntityWorld().isRemote()) return;
        final Record instance = Record.getInstance();
        final RecordManager recordManager = instance.getRecordManager();
        final LivingEntity livingEntity = event.getEntityLiving();

        final PlayTake playTake = recordManager.getEntityTake(event.getEntity().getUniqueID());
        if (playTake == null) return;

        final RecordTick tick = playTake.takeScript.playTick();

        if (tick == null)
        {
            if (playTake.shouldKill)
            {
                livingEntity.remove();
            }
            if (recordManager.stopPlaying(playTake.tapeName, playTake.takeName)) {
                MinecraftForge.EVENT_BUS.post(new RecordTapeEndEvent(playTake.tapeName, playTake));
            }
            return;
        }

        livingEntity.setPositionAndRotation(tick.posx, tick.posy, tick.posz, tick.yaw, tick.pitch);
        livingEntity.setRotationYawHead(tick.yaw);
        livingEntity.fallDistance = tick.falldistance;
        livingEntity.setSneaking(tick.isCrouching);

        if (tick.itemInHand == null) livingEntity.setHeldItem(Hand.MAIN_HAND, null);
        else livingEntity.setHeldItem(Hand.MAIN_HAND, tick.itemInHand.createItemstack());

        if (tick.isSwingInProgress && tick.swingProgress == 0f)
        {
            livingEntity.swing(Hand.MAIN_HAND, false);
        }

        if (tick.hasEvents())
        {
            tick.events.forEach(recordTickEvent -> {
                recordTickEvent.execute(livingEntity);
            });
        }
    }


}
