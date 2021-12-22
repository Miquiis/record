package me.miquiis.record.common.utils;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class MessageUtils {

    public static void sendMessage(ServerPlayerEntity player, String message)
    {
        player.sendStatusMessage(new StringTextComponent(ColorUtils.color(message)), false);
    }

}
