package me.miquiis.record.server.managers;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.miquiis.record.Record;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Record.MOD_ID)
public class CommandManager {

    public static abstract class mCommand {

        public abstract String getName();

        public abstract ArrayList<mCommand> getSubcommands();

        public abstract int execute(CommandContext<CommandSource> context);

    }

    public static abstract class mSubCommand extends mCommand {

        public abstract ArgumentType<?> getArgumentType();

        public abstract SuggestionProvider<CommandSource> getSuggestionProvider();

    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event)
    {
        dispatchCommands(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

    private final Set<mCommand> COMMANDS;

    public CommandManager()
    {
        COMMANDS = new HashSet<>();
    }

    public void registerCommand(mCommand command)
    {
        COMMANDS.add(command);
    }

    public void dispatchCommands(CommandDispatcher<CommandSource> dispatcher)
    {
        for (mCommand command : COMMANDS)
        {
            final LiteralArgumentBuilder<CommandSource> baseCommand = Commands.literal(command.getName());

            loopThroughCommands(command, baseCommand);

            dispatcher.register(baseCommand.executes(command::execute));
        }
    }

    private ArgumentBuilder<CommandSource, ?> loopThroughCommands(mCommand starterCommand, ArgumentBuilder<CommandSource, ?> baseCommand)
    {
        for (mCommand command : starterCommand.getSubcommands())
        {
            if (command instanceof mSubCommand)
            {
                mSubCommand subCommand = (mSubCommand) command;
                RequiredArgumentBuilder<CommandSource, ?> argument = Commands.argument(subCommand.getName(), subCommand.getArgumentType());

                argument = (RequiredArgumentBuilder<CommandSource, ?>) loopThroughCommands(subCommand, argument);

                argument = argument.suggests(argument.getSuggestionsProvider()).executes(subCommand::execute);
                baseCommand = baseCommand.then(argument);
            }
            else
            {
                ArgumentBuilder<CommandSource, ?> literalCommand = Commands.literal(command.getName());

                literalCommand = loopThroughCommands(command, literalCommand);

                literalCommand = literalCommand.executes(command::execute);
                baseCommand = baseCommand.then(literalCommand);
            }
        }

        return baseCommand;
    }

}
