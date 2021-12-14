package me.miquiis.record.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.miquiis.record.Record;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecordCommand {
    public RecordCommand(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("record").then(Commands.literal("start").then(Commands.argument("tape", StringArgumentType.string()).then(Commands.argument("take", StringArgumentType.string()).then(Commands.argument("entity", EntitySummonArgument.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(context -> {
            String tape = StringArgumentType.getString(context, "tape");
            String take = StringArgumentType.getString(context, "take");
            ResourceLocation entity = EntitySummonArgument.getEntityId(context, "entity");

            context.getSource().sendFeedback(new StringTextComponent("Started recording take " + take + " for tape " + tape), true);
            Record.getInstance().getRecordManager().startRecording(context.getSource().asPlayer().getUniqueID(), tape, take, entity.toString());
            return 1;
        }).then(Commands.argument("playback", StringArgumentType.string()).executes(context -> {
            String tape = StringArgumentType.getString(context, "tape");
            String take = StringArgumentType.getString(context, "take");
            ResourceLocation entity = EntitySummonArgument.getEntityId(context, "entity");

            context.getSource().sendFeedback(new StringTextComponent("Started recording take " + take + " for tape " + tape), true);
            Record.getInstance().getRecordManager().startRecording(context.getSource().asPlayer().getUniqueID(), tape, take, entity.toString());
            return play(context, false);
        })))))).then(Commands.literal("stop").executes(context -> {
            context.getSource().sendFeedback(new StringTextComponent("Recording stopped."), true);
            Record.getInstance().getRecordManager().stopRecording(context.getSource().asPlayer().getUniqueID());
            return 1;
        })).then(Commands.literal("play").then(Commands.argument("tape", StringArgumentType.string()).then(Commands.argument("shouldKill", BoolArgumentType.bool()).executes(context -> play(context, false)).then(Commands.argument("whitelist", StringArgumentType.string()).executes(context -> play(context, true))))))
        );
    }

    private int play(CommandContext<CommandSource> context, boolean hasWhitelist) throws CommandSyntaxException {
        String tape = StringArgumentType.getString(context, "tape");
        boolean shouldKill = BoolArgumentType.getBool(context, "shouldKill");
        List<String> whitelist = hasWhitelist ? new ArrayList<>(Arrays.asList(StringArgumentType.getString(context, "whitelist").split(","))) : new ArrayList<>();
        return Record.getInstance().getRecordManager().startPlaying(context.getSource().asPlayer(), tape, shouldKill, whitelist);
    }

}
