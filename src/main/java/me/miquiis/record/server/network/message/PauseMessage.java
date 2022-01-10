package me.miquiis.record.server.network.message;

import me.miquiis.record.Record;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PauseMessage {

    public PauseMessage() {
    }

    public static void encode(PauseMessage message, PacketBuffer buffer)
    {

    }

    public static PauseMessage decode(PacketBuffer buffer)
    {
        return new PauseMessage();
    }

    public static void handle(PauseMessage message, Supplier<NetworkEvent.Context> contextSupplier)
    {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity sender = contextSupplier.get().getSender();
            Record.getInstance().getRecordManager().handlePause(sender);
        });
        context.setPacketHandled(true);
    }


}
