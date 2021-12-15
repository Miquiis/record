package me.miquiis.record.common.events;

import me.miquiis.record.Record;
import me.miquiis.record.common.managers.RecordManager;
import me.miquiis.record.common.models.PlayTake;
import me.miquiis.record.common.models.RecordScript;
import me.miquiis.record.server.commands.HelloWorldCommand;
import me.miquiis.record.server.commands.RecordCommand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = Record.MOD_ID)
public class ForgeEvents {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event)
    {
        new HelloWorldCommand(event.getDispatcher());
        new RecordCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.PlayerTickEvent event)
    {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END)
        {
            final Record instance = Record.getInstance();
            final RecordManager recordManager = instance.getRecordManager();

            if (!recordManager.isRecording((event.player.getUniqueID()))) return;

            recordManager.getRecordingTake(event.player.getUniqueID()).recordScript.addTick(new RecordScript.RecordTick(event.player));
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

        final RecordScript.RecordTick tick = playTake.takeScript.playTick();

        if (tick == null)
        {
            if (playTake.shouldKill)
            {
                livingEntity.remove();
            }
            if (recordManager.stopPlaying(playTake.tapeName, playTake.takeName)) {
                System.out.println("Animation has ended");
            }
            return;
        }

        livingEntity.setPositionAndRotation(tick.posx, tick.posy, tick.posz, tick.yaw, tick.pitch);
        livingEntity.setRotationYawHead(tick.yaw);
        livingEntity.fallDistance = tick.falldistance;

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
