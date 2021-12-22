package me.miquiis.record.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.miquiis.record.Record;
import me.miquiis.record.common.managers.RecordManager;
import me.miquiis.record.common.utils.MessageUtils;
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

            final RecordManager recordManager = Record.getInstance().getRecordManager();

            if (recordManager.isSendingFeedback())
                MessageUtils.sendMessage(context.getSource().asPlayer(), "&eStarted recording take " + take + " for tape " + tape);

            recordManager.startRecording(context.getSource().asPlayer().getUniqueID(), tape, take, entity.toString());
            return 1;
        }).then(Commands.argument("playback", StringArgumentType.string()).executes(context -> {
            String tape = StringArgumentType.getString(context, "tape");
            String take = StringArgumentType.getString(context, "take");
            ResourceLocation entity = EntitySummonArgument.getEntityId(context, "entity");

            final RecordManager recordManager = Record.getInstance().getRecordManager();

            if (recordManager.isSendingFeedback())
                MessageUtils.sendMessage(context.getSource().asPlayer(), "&eStarted recording take " + take + " for tape " + tape);

            recordManager.startRecording(context.getSource().asPlayer().getUniqueID(), tape, take, entity.toString());
            return play(context, false);
        })))))).then(Commands.literal("stop").executes(context -> {
            final RecordManager recordManager = Record.getInstance().getRecordManager();

            if (recordManager.isSendingFeedback())
                MessageUtils.sendMessage(context.getSource().asPlayer(), "&cRecording stopped.");

            recordManager.stopRecording(context.getSource().asPlayer().getUniqueID());
            return 1;
        })).then(Commands.literal("play").then(Commands.argument("tape", StringArgumentType.string()).then(Commands.argument("shouldKill", BoolArgumentType.bool()).executes(context -> play(context, false)).then(Commands.argument("whitelist", StringArgumentType.string()).executes(context -> play(context, true))))))
        .then(Commands.literal("feedback").executes(context -> {
            MessageUtils.sendMessage(context.getSource().asPlayer(), "&eFeedback commands are now " + Record.getInstance().getRecordManager().toggleFeedback());
            return 1;
        }))
        .then(Commands.literal("addEvent").then(Commands.argument("label", StringArgumentType.string()).executes(context -> createCustomEvent(context, false))
                .then(Commands.argument("value", StringArgumentType.string()).executes(context -> createCustomEvent(context, true))))
        )
        );
    }

    private int play(CommandContext<CommandSource> context, boolean hasWhitelist) throws CommandSyntaxException {
        String tape = StringArgumentType.getString(context, "tape");
        boolean shouldKill = BoolArgumentType.getBool(context, "shouldKill");
        List<String> whitelist = hasWhitelist ? new ArrayList<>(Arrays.asList(StringArgumentType.getString(context, "whitelist").split(","))) : new ArrayList<>();
        return Record.getInstance().getRecordManager().startPlaying(context.getSource().asPlayer(), tape, shouldKill, whitelist);
    }

    private int createCustomEvent(CommandContext<CommandSource> context, boolean hasValue) throws CommandSyntaxException {
        String label = StringArgumentType.getString(context, "label");
        String value = hasValue ? StringArgumentType.getString(context, "value") : null;
        return Record.getInstance().getRecordManager().createCustomEvent(context.getSource().asPlayer(), label, value);
    }

}
