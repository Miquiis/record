package me.miquiis.record.server.commands;

import com.mojang.brigadier.context.CommandContext;
import me.miquiis.record.server.managers.CommandManager;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;

public class RecordCommand extends CommandManager.mCommand {

    class StartCommand extends CommandManager.mCommand {

        @Override
        public String getName() {
            return "start";
        }

        @Override
        public ArrayList<CommandManager.mCommand> getSubcommands() {
            return null;
        }

        @Override
        public int execute(CommandContext<CommandSource> context) {
            return 0;
        }
    }

    @Override
    public String getName() {
        return "record";
    }

    @Override
    public ArrayList<CommandManager.mCommand> getSubcommands() {
        return new ArrayList<CommandManager.mCommand>(){{

        }};
    }

    @Override
    public int execute(CommandContext<CommandSource> context) {
        return 0;
    }
}
