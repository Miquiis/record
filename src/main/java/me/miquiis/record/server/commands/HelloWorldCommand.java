package me.miquiis.record.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.miquiis.record.common.utils.ColorUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class HelloWorldCommand {
    public HelloWorldCommand(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("helloworld").executes(command -> helloWorld(command.getSource())));
    }

    private int helloWorld(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.asPlayer();
        player.sendMessage(new StringTextComponent(ColorUtils.color("&cHello, &bworld!")), null);
        System.out.println(source.getWorld().isRemote);
        System.out.println(source.getEntity().getEntityWorld().isRemote);
        return 1;
    }

}
