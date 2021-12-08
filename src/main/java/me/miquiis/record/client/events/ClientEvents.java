package me.miquiis.record.client.events;

import me.miquiis.record.client.ClientKeybinds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEvents {

    @SubscribeEvent
    public static void onPress(final InputEvent.KeyInputEvent event) {
        final KeyBinding[] keyBindings = ClientKeybinds.keyBindings;
        if (keyBindings[0].isKeyDown()) {
//            Minecraft.getInstance().displayGuiScreen(new TestGUI(new StringTextComponent("Test?")));
        }
    }

}
