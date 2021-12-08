package me.miquiis.record.client;

import net.minecraft.client.gui.widget.list.KeyBindingList;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientKeybinds {

    public static KeyBinding[] keyBindings;

    public static void registerBindings() {
        ClientKeybinds.keyBindings[0] = new KeyBinding("key.record.start.desc", 82, "key.record.group");
        ClientKeybinds.keyBindings[1] = new KeyBinding("key.record.stop.desc", 89, "key.record.group");
        for (final KeyBinding keyBinding : ClientKeybinds.keyBindings) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }

    static {
        ClientKeybinds.keyBindings = new KeyBinding[2];
    }
}
