package me.miquiis.record.server.network.message;

import me.miquiis.record.Record;
import me.miquiis.record.common.managers.RecordManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class StopRecordMessage {

    public StopRecordMessage() {
    }

    public static void encode(StopRecordMessage message, PacketBuffer buffer)
    {

    }

    public static StopRecordMessage decode(PacketBuffer buffer)
    {
        return new StopRecordMessage();
    }

    public static void handle(StopRecordMessage message, Supplier<NetworkEvent.Context> contextSupplier)
    {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity sender = contextSupplier.get().getSender();
            Record.getInstance().getRecordManager().stopRecording(sender);
        });
        context.setPacketHandled(true);
    }


}
