package me.miquiis.record.client.events;

import me.miquiis.record.Record;
import me.miquiis.record.client.ClientKeybinds;
import me.miquiis.record.server.network.RecordNetwork;
import me.miquiis.record.server.network.message.PauseMessage;
import me.miquiis.record.server.network.message.StopRecordMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Record.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onPress(final InputEvent.KeyInputEvent event) {
        final KeyBinding[] keyBindings = ClientKeybinds.keyBindings;
        if (keyBindings[0].isKeyDown()) {
            RecordNetwork.CHANNEL.sendToServer(new StopRecordMessage());
        } else if (keyBindings[1].isPressed()) {
            RecordNetwork.CHANNEL.sendToServer(new PauseMessage());
        }
    }

}
